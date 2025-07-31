package com.project.banking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.banking.dto.TransactionRequest;
import com.project.banking.model.User;
import com.project.banking.repository.UserRepository;
import com.project.banking.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class BankingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User sender;
    private User recipient;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        sender = new User("sender1", "Alice", "alice@example.com", 1000.0);
        recipient = new User("recipient1", "Bob", "bob@example.com", 500.0);

        userRepository.save(sender);
        userRepository.save(recipient);
    }

    @Test
    void fullTransactionFlow_ShouldWorkEndToEnd() throws Exception {
        // Create transaction request
        TransactionRequest request = new TransactionRequest("sender1", "recipient1", 200.0);

        // Perform transaction
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verify balances updated
        User updatedSender = userRepository.findById("sender1").orElse(null);
        User updatedRecipient = userRepository.findById("recipient1").orElse(null);

        assertNotNull(updatedSender);
        assertNotNull(updatedRecipient);
        assertEquals(800.0, updatedSender.getBalance());
        assertEquals(700.0, updatedRecipient.getBalance());

        // Verify transaction exists
        assertEquals(1, transactionRepository.count());
    }

    @Test
    void createUser_ThenPerformTransaction_ShouldWork() throws Exception {
        // Create new user
        User newUser = new User(null, "Charlie", "charlie@example.com", 2000.0);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);

        // Perform transaction with new user
        TransactionRequest request = new TransactionRequest(createdUser.getId(), "recipient1", 300.0);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verify
        User updatedNewUser = userRepository.findById(createdUser.getId()).orElse(null);
        User updatedRecipient = userRepository.findById("recipient1").orElse(null);

        assertNotNull(updatedNewUser);
        assertNotNull(updatedRecipient);
        assertEquals(1700.0, updatedNewUser.getBalance());
        assertEquals(800.0, updatedRecipient.getBalance());
    }

    @Test
    void insufficientBalance_ShouldReturnBadRequest() throws Exception {
        TransactionRequest request = new TransactionRequest("sender1", "recipient1", 1500.0);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verify balances unchanged
        User unchangedSender = userRepository.findById("sender1").orElse(null);
        User unchangedRecipient = userRepository.findById("recipient1").orElse(null);

        assertNotNull(unchangedSender);
        assertNotNull(unchangedRecipient);
        assertEquals(1000.0, unchangedSender.getBalance());
        assertEquals(500.0, unchangedRecipient.getBalance());
    }
}