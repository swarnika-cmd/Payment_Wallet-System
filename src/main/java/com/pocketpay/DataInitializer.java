package com.pocketpay;

import com.pocketpay.entity.User;
import com.pocketpay.entity.Wallet;
import com.pocketpay.repository.UserRepository;
import com.pocketpay.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, WalletRepository walletRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Create Arjun (The Sender)
            if (userRepository.findByMobileNumber("9876543210").isEmpty()) {
                // Password: "password"
                User arjun = new User("Arjun", "9876543210", passwordEncoder.encode("password"), "ROLE_USER");
                userRepository.save(arjun);

                // Give Arjun 1000 Rupees
                Wallet arjunWallet = new Wallet(new java.math.BigDecimal("1000.0"), arjun);
                walletRepository.save(arjunWallet);
                arjun.setWallet(arjunWallet);
                userRepository.save(arjun);
            }

            // 2. Create Radha (The Receiver)
            if (userRepository.findByMobileNumber("9123456789").isEmpty()) {
                // Password: "password"
                User radha = new User("Radha", "9123456789", passwordEncoder.encode("password"), "ROLE_USER");
                userRepository.save(radha);

                // Give Radha 0 Rupees
                Wallet radhaWallet = new Wallet(java.math.BigDecimal.ZERO, radha);
                walletRepository.save(radhaWallet);
                radha.setWallet(radhaWallet);
                userRepository.save(radha);
            }

            System.out.println("Data Initialized: Arjun and Radha with Passwords are ready.");
        };
    }
}
