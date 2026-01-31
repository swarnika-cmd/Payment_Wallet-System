package com.pocketpay.controller;

import com.pocketpay.dto.AuthRequest;
import com.pocketpay.dto.AuthResponse;
import com.pocketpay.dto.RegisterRequest;
import com.pocketpay.entity.User;
import com.pocketpay.entity.Wallet;
import com.pocketpay.repository.UserRepository;
import com.pocketpay.repository.WalletRepository;
import com.pocketpay.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.pocketpay.service.NotificationService notificationService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
            UserRepository userRepository, WalletRepository walletRepository,
            PasswordEncoder passwordEncoder, com.pocketpay.service.NotificationService notificationService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 1. Check if user exists
        if (userRepository.findByMobileNumber(request.getMobileNumber()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Mobile number already registered!");
        }

        // 2. Create User
        User newUser = new User(
                request.getName(),
                request.getMobileNumber(),
                passwordEncoder.encode(request.getPassword()),
                "ROLE_USER");
        newUser.setEmail(request.getEmail());
        userRepository.save(newUser);

        // 3. Create Piggybank Wallet (Balance 0.0)
        Wallet newWallet = new Wallet(java.math.BigDecimal.ZERO, newUser);
        walletRepository.save(newWallet);
        newUser.setWallet(newWallet);
        userRepository.save(newUser);

        // 4. Send Welcome Email
        notificationService.sendWelcomeEmail(request.getMobileNumber(), request.getName());

        return ResponseEntity.ok("User registered successfully with 0.0 balance. Please login.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // 1. Authenticate Request
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMobileNumber(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Generate Token
        String jwt = jwtUtils.generateToken(request.getMobileNumber());

        // 3. Return Response
        return ResponseEntity.ok(new AuthResponse(jwt, request.getMobileNumber()));
    }
}
