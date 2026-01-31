package com.pocketpay.controller;

import com.pocketpay.dto.TransferRequest;
import com.pocketpay.entity.Transaction;
import com.pocketpay.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@Valid @RequestBody TransferRequest request,
            @io.swagger.v3.oas.annotations.Parameter(hidden = true) Authentication authentication) {
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
            @io.swagger.v3.oas.annotations.Parameter(hidden = true) Authentication authentication,
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
}
