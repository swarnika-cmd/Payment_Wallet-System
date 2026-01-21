package com.pocketpay.service;

import com.pocketpay.entity.Transaction;
import com.pocketpay.entity.User;
import com.pocketpay.entity.Wallet;
import com.pocketpay.repository.TransactionRepository;
import com.pocketpay.repository.UserRepository;
import com.pocketpay.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    public void transferMoney(String senderMobile, String receiverMobile, Double amount) {
        // 1. Validate Amount
        if (amount <= 0) {
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
        if (senderWallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient Balance: " + senderWallet.getBalance() + " < " + amount);
        }

        // 5. The Deduction (Atomic)
        senderWallet.setBalance(senderWallet.getBalance() - amount);

        // 6. The Addition (Atomic)
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);

        // 7. Save Changes
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // 8. Record Transaction
        Transaction transaction = new Transaction(
                senderMobile,
                receiverMobile,
                amount,
                LocalDateTime.now(),
                "SUCCESS");
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
