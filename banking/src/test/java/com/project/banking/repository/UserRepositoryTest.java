//package com.project.banking.repository;
//
//
//import com.project.banking.model.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//@DataJpaTest
//@ActiveProfiles("test")
//public class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        testUser = new User("user1", "John Doe", "john@example.com", 1000.0);
//        userRepository.save(testUser);
//    }
//
//    @Test
//    void updateUserBalance_ShouldUpdateBalance() {
//        int updatedRows = userRepository.updateUserBalance("user1", 1500.0);
//
//        assertEquals(1, updatedRows);
//        User updated = userRepository.findById("user1").orElse(null);
//        assertNotNull(updated);
//        assertEquals(1500.0, updated.getBalance());
//    }
//
//    @Test
//    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
//        boolean exists = userRepository.existsByEmail("john@example.com");
//        assertTrue(exists);
//    }
//
//    @Test
//    void existsByEmail_WhenEmailNotExists_ShouldReturnFalse() {
//        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
//        assertFalse(exists);
//    }
//}