package com.pocketpay.dto;

public class AuthResponse {
    private String token;
    private String mobileNumber;

    public AuthResponse(String token, String mobileNumber) {
        this.token = token;
        this.mobileNumber = mobileNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
