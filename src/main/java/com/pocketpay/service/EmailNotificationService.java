package com.pocketpay.service;

import com.pocketpay.entity.Transaction;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService {

    @Override
    public void sendTransactionAlert(String toMobile, Transaction transaction) {
        // In a real app, this would use JavaMailSender
        System.out.println("--------------------------------------------------");
        System.out.println("EMAIL SENT TO: " + toMobile);
        System.out.println("SUBJECT: Transaction Alert");
        System.out.println("BODY: A transaction of " + transaction.getAmount() + " was " + transaction.getStatus());
        System.out.println("--------------------------------------------------");
    }

    @Override
    public void sendWelcomeEmail(String toMobile, String name) {
        System.out.println("--------------------------------------------------");
        System.out.println("EMAIL SENT TO: " + toMobile);
        System.out.println("SUBJECT: Welcome to PocketPay");
        System.out.println("BODY: Hello " + name + ", your account is created!");
        System.out.println("--------------------------------------------------");
    }
}
