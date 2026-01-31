package com.pocketpay.service;

import com.pocketpay.entity.Transaction;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService {

    private final org.springframework.mail.javamail.JavaMailSender mailSender;
    private final com.pocketpay.repository.UserRepository userRepository;

    public EmailNotificationService(org.springframework.mail.javamail.JavaMailSender mailSender,
            com.pocketpay.repository.UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    @Override
    @org.springframework.scheduling.annotation.Async
    public void sendTransactionAlert(String mobileNumber, Transaction transaction) {
        userRepository.findByMobileNumber(mobileNumber).ifPresent(user -> {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                sendEmail(
                        user.getEmail(),
                        "Transaction Alert: " + transaction.getType(),
                        "Dear " + user.getName() + ",\n\n" +
                                "A transaction of Rs. " + transaction.getAmount() + " was " + transaction.getStatus()
                                + ".\n" +
                                "Reference ID: " + transaction.getReferenceId() + "\n" +
                                "Description: " + transaction.getDescription() + "\n\n" +
                                "Current Balance: " + user.getWallet().getBalance() // This might be slightly stale if
                                                                                    // async, but acceptable
                );
            }
        });
    }

    @Override
    @org.springframework.scheduling.annotation.Async
    public void sendWelcomeEmail(String mobileNumber, String name) {
        userRepository.findByMobileNumber(mobileNumber).ifPresent(user -> {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                sendEmail(
                        user.getEmail(),
                        "Welcome to PocketPay!",
                        "Dear " + name + ",\n\n" +
                                "Welcome to PocketPay! Your digital wallet is ready.\n" +
                                "Login now to start sending money.\n\n" +
                                "Cheers,\nPocketPay Team");
            }
        });
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setFrom("noreply@pocketpay.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("EMAIL SENT TO: " + to);
        } catch (Exception e) {
            System.err.println("FAILED TO SEND EMAIL TO: " + to + " Error: " + e.getMessage());
        }
    }
}
