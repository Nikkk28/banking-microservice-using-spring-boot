//package com.project.banking.service;
//
//import com.project.banking.model.Transaction;
//import com.project.banking.model.User;
//import com.project.banking.repository.TransactionRepository;
//import com.project.banking.repository.UserRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TransactionServiceTest {
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private TransactionService transactionService;
//
//    private User sender;
//    private User recipient;
//    private Transaction transaction;
//
//    @BeforeEach
//    void setUp() {
//        sender = new User("sender1", "Alice", "alice@example.com", 1000.0);
//        recipient = new User("recipient1", "Bob", "bob@example.com", 500.0);
//        transaction = new Transaction(sender, recipient, 200.0);
//    }
//
//    @Test
//    void getAllTransactions_ShouldReturnAllTransactions() {
//        // Given
//        List<Transaction> transactions = Arrays.asList(transaction);
//        when(transactionRepository.findAllWithUsers()).thenReturn(transactions);
//
//        // When
//        List<Transaction> result = transactionService.getAllTransactions();
//
//        // Then
//        assertEquals(1, result.size());
//        verify(transactionRepository).findAllWithUsers();
//    }
//
//    @Test
//    void createTransaction_WithValidData_ShouldCreateTransaction() {
//        // Given
//        when(userRepository.findById("sender1")).thenReturn(Optional.of(sender));
//        when(userRepository.findById("recipient1")).thenReturn(Optional.of(recipient));
//        when(userRepository.save(sender)).thenReturn(sender);
//        when(userRepository.save(recipient)).thenReturn(recipient);
//        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
//
//        // When
//        Transaction result = transactionService.createTransaction("sender1", "recipient1", 200.0);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(800.0, sender.getBalance()); // 1000 - 200
//        assertEquals(700.0, recipient.getBalance()); // 500 + 200
//        verify(userRepository).save(sender);
//        verify(userRepository).save(recipient);
//        verify(transactionRepository).save(any(Transaction.class));
//    }
//
//    @Test
//    void createTransaction_WithSameUser_ShouldThrowException() {
//        // When & Then
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> transactionService.createTransaction("user1", "user1", 100.0)
//        );
//        assertEquals("Sender and recipient cannot be the same", exception.getMessage());
//    }
//
//    @Test
//    void createTransaction_WithNonExistentSender_ShouldThrowException() {
//        // Given
//        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());
//
//        // When & Then
//        EntityNotFoundException exception = assertThrows(
//                EntityNotFoundException.class,
//                () -> transactionService.createTransaction("nonexistent", "recipient1", 100.0)
//        );
//        assertEquals("Sender not found with id: nonexistent", exception.getMessage());
//    }
//
//    @Test
//    void createTransaction_WithNonExistentRecipient_ShouldThrowException() {
//        // Given
//        when(userRepository.findById("sender1")).thenReturn(Optional.of(sender));
//        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());
//
//        // When & Then
//        EntityNotFoundException exception = assertThrows(
//                EntityNotFoundException.class,
//                () -> transactionService.createTransaction("sender1", "nonexistent", 100.0)
//        );
//        assertEquals("Recipient not found with id: nonexistent", exception.getMessage());
//    }
//
//    @Test
//    void createTransaction_WithZeroAmount_ShouldThrowException() {
//        // When & Then
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> transactionService.createTransaction("sender1", "recipient1", 0.0)
//        );
//        assertEquals("Transaction amount must be greater than 0", exception.getMessage());
//    }
//
//    @Test
//    void createTransaction_WithNegativeAmount_ShouldThrowException() {
//        // When & Then
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> transactionService.createTransaction("sender1", "recipient1", -100.0)
//        );
//        assertEquals("Transaction amount must be greater than 0", exception.getMessage());
//    }
//
//    @Test
//    void createTransaction_WithInsufficientBalance_ShouldThrowException() {
//        // Given
//        when(userRepository.findById("sender1")).thenReturn(Optional.of(sender));
//        when(userRepository.findById("recipient1")).thenReturn(Optional.of(recipient));
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> transactionService.createTransaction("sender1", "recipient1", 1500.0)
//        );
//        assertEquals("Insufficient balance. Available: 1000.0, Required: 1500.0", exception.getMessage());
//    }
//
//    @Test
//    void getTransactionsByUserId_WithValidUser_ShouldReturnTransactions() {
//        // Given
//        List<Transaction> transactions = Arrays.asList(transaction);
//        when(userRepository.existsById("user1")).thenReturn(true);
//        when(transactionRepository.findByUserId("user1")).thenReturn(transactions);
//
//        // When
//        List<Transaction> result = transactionService.getTransactionsByUserId("user1");
//
//        // Then
//        assertEquals(1, result.size());
//        verify(transactionRepository).findByUserId("user1");
//    }
//
//    @Test
//    void getTransactionsByUserId_WithNonExistentUser_ShouldThrowException() {
//        // Given
//        when(userRepository.existsById("nonexistent")).thenReturn(false);
//
//        // When & Then
//        EntityNotFoundException exception = assertThrows(
//                EntityNotFoundException.class,
//                () -> transactionService.getTransactionsByUserId("nonexistent")
//        );
//        assertEquals("User not found with id: nonexistent", exception.getMessage());
//    }
//}