package com.project.banking.service;

import com.project.banking.model.Transaction;
import com.project.banking.model.User;
import com.project.banking.repository.TransactionRepository;
import com.project.banking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllWithUsers();
    }
    
    public Transaction createTransaction(String senderId, String recipientId, double amount) {
        // Validation: Check if sender and recipient exist
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found with id: " + senderId));
        
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new EntityNotFoundException("Recipient not found with id: " + recipientId));
        
        // Validation: Check if sender and recipient are different
        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("Sender and recipient cannot be the same");
        }
        
        // Validation: Check amount
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than 0");
        }
        
        // Validation: Check sender balance
        if (sender.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance. Available: " + sender.getBalance() + ", Required: " + amount);
        }
        
        // Atomic balance updates
        double newSenderBalance = sender.getBalance() - amount;
        double newRecipientBalance = recipient.getBalance() + amount;
        
        sender.setBalance(newSenderBalance);
        recipient.setBalance(newRecipientBalance);
        
        // Save updated balances
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // Create and save transaction
        Transaction transaction = new Transaction(sender, recipient, amount);
        return transactionRepository.save(transaction);
    }
    
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByUserId(String userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        
        return transactionRepository.findByUserId(userId);
    }
}
