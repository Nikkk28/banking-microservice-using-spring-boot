package com.project.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class TransactionRequest {
    
    @NotBlank(message = "Sender ID is required")
    private String senderId;
    
    @NotBlank(message = "Recipient ID is required")
    private String recipientId;
    
    @Positive(message = "Amount must be greater than 0")
    private double amount;
    
    // Constructors
    public TransactionRequest() {}
    
    public TransactionRequest(String senderId, String recipientId, double amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }
    
    // Getters and Setters
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public String getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
