package com.pocketpay.dto;

import com.pocketpay.enums.TransactionStatus;
import com.pocketpay.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private Long id;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private String description;
    private String referenceId;
    private LocalDateTime createdAt;

    // Optional info
    private String otherPartyName;
    private String otherPartyMobile;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, TransactionType type, TransactionStatus status, BigDecimal amount,
            String description, String referenceId, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.description = description;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getOtherPartyName() {
        return otherPartyName;
    }

    public void setOtherPartyName(String otherPartyName) {
        this.otherPartyName = otherPartyName;
    }

    public String getOtherPartyMobile() {
        return otherPartyMobile;
    }

    public void setOtherPartyMobile(String otherPartyMobile) {
        this.otherPartyMobile = otherPartyMobile;
    }
}
