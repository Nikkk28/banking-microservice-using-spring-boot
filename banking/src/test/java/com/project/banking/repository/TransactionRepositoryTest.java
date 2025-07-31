package com.project.banking.repository;

import com.project.banking.model.Transaction;
import com.project.banking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private User sender;
    private User recipient;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        sender = new User("sender1", "Alice", "alice@example.com", 1000.0);
        recipient = new User("recipient1", "Bob", "bob@example.com", 500.0);

        userRepository.save(sender);
        userRepository.save(recipient);

        transaction = new Transaction(sender, recipient, 200.0);
        transactionRepository.save(transaction);
    }

    @Test
    void findAllWithUsers_ShouldReturnTransactionsWithUsers() {
        List<Transaction> transactions = transactionRepository.findAllWithUsers();

        assertEquals(1, transactions.size());
        Transaction retrieved = transactions.get(0);
        assertNotNull(retrieved.getSender());
        assertNotNull(retrieved.getRecipient());
        assertEquals("Alice", retrieved.getSender().getName());
        assertEquals("Bob", retrieved.getRecipient().getName());
    }

    @Test
    void findByUserId_ShouldReturnUserTransactions() {
        List<Transaction> senderTransactions = transactionRepository.findByUserId("sender1");
        List<Transaction> recipientTransactions = transactionRepository.findByUserId("recipient1");

        assertEquals(1, senderTransactions.size());
        assertEquals(1, recipientTransactions.size());
        assertEquals(transaction.getId(), senderTransactions.get(0).getId());
        assertEquals(transaction.getId(), recipientTransactions.get(0).getId());
    }
}

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("user1", "John Doe", "john@example.com", 1000.0);
        userRepository.save(testUser);
    }

    @Test
    void updateUserBalance_ShouldUpdateBalance() {
        int updatedRows = userRepository.updateUserBalance("user1", 1500.0);

        assertEquals(1, updatedRows);
        User updated = userRepository.findById("user1").orElse(null);
        assertNotNull(updated);
        assertEquals(1500.0, updated.getBalance());
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        boolean exists = userRepository.existsByEmail("john@example.com");
        assertTrue(exists);
    }

    @Test
    void existsByEmail_WhenEmailNotExists_ShouldReturnFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        assertFalse(exists);
    }
}