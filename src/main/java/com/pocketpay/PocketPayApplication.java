package com.pocketpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PocketPayApplication {

    public static void main(String[] args) {
        // Fix for "FATAL: invalid value for parameter TimeZone: Asia/Calcutta"
        // Force the application to use a standard TimeZone that Postgres accepts.
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        SpringApplication.run(PocketPayApplication.class, args);
    }

}
