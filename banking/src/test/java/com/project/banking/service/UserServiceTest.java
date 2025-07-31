package com.project.banking.service;

import com.project.banking.model.User;
import com.project.banking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("user1", "John Doe", "john@example.com", 1000.0);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        List<User> users = Arrays.asList(testUser, new User("user2", "Jane Doe", "jane@example.com", 500.0));
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserById("user1");

        // Then
        assertEquals(testUser, result);
        verify(userRepository).findById("user1");
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowException() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserById("nonexistent")
        );
        assertEquals("User not found with id: nonexistent", exception.getMessage());
    }

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        // Given
        User newUser = new User(null, "Alice Smith", "alice@example.com", 750.0);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result.getId()); // UUID should be generated
        assertEquals("Alice Smith", result.getName());
        verify(userRepository).existsByEmail("alice@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(testUser)
        );
        assertEquals("User with email john@example.com already exists", exception.getMessage());
    }

    @Test
    void createUser_WithNegativeBalance_ShouldSetBalanceToZero() {
        // Given
        User userWithNegativeBalance = new User("user3", "Bob Wilson", "bob@example.com", -100.0);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(userWithNegativeBalance);

        // When
        userService.createUser(userWithNegativeBalance);

        // Then
        verify(userRepository).save(argThat(user -> user.getBalance() == 0.0));
    }

    @Test
    void updateBalance_WithValidData_ShouldUpdateBalance() {
        // Given
        when(userRepository.existsById("user1")).thenReturn(true);
        when(userRepository.updateUserBalance("user1", 1500.0)).thenReturn(1);

        // When
        assertDoesNotThrow(() -> userService.updateBalance("user1", 1500.0));

        // Then
        verify(userRepository).updateUserBalance("user1", 1500.0);
    }

    @Test
    void updateBalance_WithNegativeAmount_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateBalance("user1", -100.0)
        );
        assertEquals("Balance cannot be negative", exception.getMessage());
    }

    @Test
    void updateBalance_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(userRepository.existsById("nonexistent")).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateBalance("nonexistent", 1000.0)
        );
        assertEquals("User not found with id: nonexistent", exception.getMessage());
    }

    @Test
    void userExists_WhenUserExists_ShouldReturnTrue() {
        // Given
        when(userRepository.existsById("user1")).thenReturn(true);

        // When
        boolean result = userService.userExists("user1");

        // Then
        assertTrue(result);
    }

    @Test
    void userExists_WhenUserNotExists_ShouldReturnFalse() {
        // Given
        when(userRepository.existsById("nonexistent")).thenReturn(false);

        // When
        boolean result = userService.userExists("nonexistent");

        // Then
        assertFalse(result);
    }
}