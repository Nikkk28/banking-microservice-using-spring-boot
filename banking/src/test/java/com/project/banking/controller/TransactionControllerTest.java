package com.project.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.banking.dto.TransactionRequest;
import com.project.banking.model.Transaction;
import com.project.banking.model.User;
import com.project.banking.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction transaction;
    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        User sender = new User("sender1", "Alice", "alice@example.com", 1000.0);
        User recipient = new User("recipient1", "Bob", "bob@example.com", 500.0);
        transaction = new Transaction(sender, recipient, 200.0);
        transactionRequest = new TransactionRequest("sender1", "recipient1", 200.0);
    }

    @Test
    void getAllTransactions_ShouldReturnTransactionsList() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // When & Then
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(transactionService).getAllTransactions();
    }

    @Test
    void createTransaction_WithValidData_ShouldCreateTransaction() throws Exception {
        // Given
        when(transactionService.createTransaction(anyString(), anyString(), anyDouble()))
                .thenReturn(transaction);

        // When & Then
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated());

        verify(transactionService).createTransaction("sender1", "recipient1", 200.0);
    }

    @Test
    void createTransaction_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        TransactionRequest invalidRequest = new TransactionRequest("", "", -100.0);

        // When & Then
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransactionsByUserId_ShouldReturnUserTransactions() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getTransactionsByUserId("user1")).thenReturn(transactions);

        // When & Then
        mockMvc.perform(get("/api/transactions/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(transactionService).getTransactionsByUserId("user1");
    }
}