package com.pocketpay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class TransferRequest {
    @NotBlank(message = "Sender mobile is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid sender mobile number")
    private String senderMobile;

    @NotBlank(message = "Receiver mobile is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid receiver mobile number")
    private String receiverMobile;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Minimum transfer amount is 1.0")
    private Double amount;

    public TransferRequest() {
    }

    public TransferRequest(String senderMobile, String receiverMobile, Double amount) {
        this.senderMobile = senderMobile;
        this.receiverMobile = receiverMobile;
        this.amount = amount;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
