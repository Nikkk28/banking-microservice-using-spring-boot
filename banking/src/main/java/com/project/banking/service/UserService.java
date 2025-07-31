package com.project.banking.service;

import com.project.banking.model.User;
import com.project.banking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }
    
    public User createUser(User user) {
        // Generate UUID if not provided
        if (user.getId() == null || user.getId().trim().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        
        // Ensure balance is non-negative
        if (user.getBalance() < 0) {
            user.setBalance(0.0);
        }
        
        return userRepository.save(user);
    }
    
    public void updateBalance(String userId, double newBalance) {
        if (newBalance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        
        int updatedRows = userRepository.updateUserBalance(userId, newBalance);
        if (updatedRows == 0) {
            throw new EntityNotFoundException("Failed to update balance for user: " + userId);
        }
    }
    
    @Transactional(readOnly = true)
    public boolean userExists(String userId) {
        return userRepository.existsById(userId);
    }
}
