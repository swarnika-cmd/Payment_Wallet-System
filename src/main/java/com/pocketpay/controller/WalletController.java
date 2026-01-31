package com.pocketpay.controller;

import com.pocketpay.dto.AddMoneyRequest;
import com.pocketpay.dto.TransactionResponse;
import com.pocketpay.dto.TransferRequest;
import com.pocketpay.dto.WithdrawRequest;
import com.pocketpay.entity.Transaction;
import com.pocketpay.service.WalletService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/add-money")
    public ResponseEntity<TransactionResponse> addMoney(@Valid @RequestBody AddMoneyRequest request,
            @Parameter(hidden = true) Authentication authentication) {
        Transaction txn = walletService.addMoney(
                authentication.getName(), // mobile number
                request.getAmount(),
                request.getPaymentMethod(),
                request.getDescription());
        return ResponseEntity.ok(mapToResponse(txn));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdrawMoney(@Valid @RequestBody WithdrawRequest request,
            @Parameter(hidden = true) Authentication authentication) {
        Transaction txn = walletService.withdrawMoney(
                authentication.getName(),
                request.getAmount(),
                request.getBankAccount(),
                request.getDescription());
        return ResponseEntity.ok(mapToResponse(txn));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@Valid @RequestBody TransferRequest request,
            @Parameter(hidden = true) Authentication authentication) {
        // Validate that the logged-in user is the sender
        String loggedInMobile = authentication.getName();
        if (!loggedInMobile.equals(request.getSenderMobile())) {
            return ResponseEntity.status(403).body("You can only transfer from your own wallet");
        }

        walletService.transferMoney(
                request.getSenderMobile(),
                request.getReceiverMobile(),
                request.getAmount());
        return ResponseEntity.ok("Transfer Successful");
    }

    @GetMapping("/history")
    public ResponseEntity<Page<Transaction>> getHistory(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        String loggedInMobile = authentication.getName();

        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transaction> history = walletService.getTransactionHistory(loggedInMobile, pageable);
        return ResponseEntity.ok(history);
    }

    private TransactionResponse mapToResponse(Transaction txn) {
        return new TransactionResponse(
                txn.getId(),
                txn.getType(),
                txn.getStatus(),
                txn.getAmount(),
                txn.getDescription(),
                txn.getReferenceId(),
                txn.getTimestamp());
    }
}
