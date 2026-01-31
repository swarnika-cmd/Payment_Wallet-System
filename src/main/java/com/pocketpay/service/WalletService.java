package com.pocketpay.service;

import com.pocketpay.entity.Transaction;
import com.pocketpay.entity.User;
import com.pocketpay.entity.Wallet;
import com.pocketpay.enums.TransactionStatus;
import com.pocketpay.enums.TransactionType;
import com.pocketpay.repository.TransactionRepository;
import com.pocketpay.repository.UserRepository;
import com.pocketpay.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    private final NotificationService notificationService;

    public WalletService(UserRepository userRepository, WalletRepository walletRepository,
            TransactionRepository transactionRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Transaction addMoney(String mobileNumber, BigDecimal amount, String paymentMethod, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = user.getWallet();

        // Update Balance
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        // Create Transaction
        String referenceId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(
                null, // fromWallet (External)
                wallet, // toWallet
                amount,
                TransactionType.ADD_MONEY,
                TransactionStatus.SUCCESS,
                referenceId,
                description != null ? description : "Added via " + paymentMethod);

        // Backward compatibility
        transaction.setReceiverMobile(mobileNumber);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdrawMoney(String mobileNumber, BigDecimal amount, String bankAccount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = user.getWallet();

        // Check Balance
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new com.pocketpay.exception.InsufficientBalanceException(
                    "Insufficient Balance: Available " + wallet.getBalance() + " < Required " + amount);
        }

        // Deduct Balance
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        // Create Transaction
        String referenceId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(
                wallet, // fromWallet
                null, // toWallet (External)
                amount,
                TransactionType.WITHDRAW,
                TransactionStatus.PENDING, // Withdrawals are not instant
                referenceId,
                description != null ? description : "Withdraw to " + bankAccount);

        // Backward compatibility
        transaction.setSenderMobile(mobileNumber);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void transferMoney(String senderMobile, String receiverMobile, BigDecimal amount) {
        // 1. Validate Amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        // 2. Find Users
        User sender = userRepository.findByMobileNumber(senderMobile)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findByMobileNumber(receiverMobile)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // 3. Get Wallets
        Wallet senderWallet = sender.getWallet();
        Wallet receiverWallet = receiver.getWallet();

        // 4. Check Balance (The Guard Logic)
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new com.pocketpay.exception.InsufficientBalanceException(
                    "Insufficient Balance: " + senderWallet.getBalance() + " < " + amount);
        }

        // 5. The Deduction (Atomic)
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));

        // 6. The Addition (Atomic)
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));

        // 7. Save Changes
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // 8. Record Transaction
        String referenceId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(
                senderWallet, // fromWallet
                receiverWallet, // toWallet
                amount,
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS,
                referenceId,
                "Transfer from " + sender.getName() + " to " + receiver.getName());

        // Populate legacy fields for backward compatibility
        transaction.setSenderMobile(senderMobile);
        transaction.setReceiverMobile(receiverMobile);

        transactionRepository.save(transaction);

        System.out
                .println("SUCCESS: Transferred " + amount + " from " + sender.getName() + " to " + receiver.getName());

        // 9. Send Email Notification
        notificationService.sendTransactionAlert(senderMobile, transaction);
        notificationService.sendTransactionAlert(receiverMobile, transaction);
    }

    public Page<Transaction> getTransactionHistory(String mobileNumber, Pageable pageable) {
        return transactionRepository.findBySenderMobileOrReceiverMobile(mobileNumber, mobileNumber, pageable);
    }
}
