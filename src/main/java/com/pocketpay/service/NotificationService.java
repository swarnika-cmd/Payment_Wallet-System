package com.pocketpay.service;

import com.pocketpay.entity.Transaction;

public interface NotificationService {
    void sendTransactionAlert(String toMobile, Transaction transaction);

    void sendWelcomeEmail(String toMobile, String name);
}
