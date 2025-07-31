//package com.project.banking.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.banking.model.User;
//import com.project.banking.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//
//    private ObjectMapper objectMapper;
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//        objectMapper = new ObjectMapper();
//        testUser = new User("user1", "John Doe", "john@example.com", 1000.0);
//    }
//
//    @Test
//    void getAllUsers_ShouldReturnUsersList() throws Exception {
//        // Given
//        List<User> users = Arrays.asList(testUser);
//        when(userService.getAllUsers()).thenReturn(users);
//
//        // When & Then
//        mockMvc.perform(get("/api/users"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1))
//                .andExpect(jsonPath("$[0].name").value("John Doe"));
//
//        verify(userService).getAllUsers();
//    }
//
//    @Test
//    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
//        // Given
//        when(userService.getUserById("user1")).thenReturn(testUser);
//
//        // When & Then
//        mockMvc.perform(get("/api/users/user1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("John Doe"))
//                .andExpect(jsonPath("$.email").value("john@example.com"));
//
//        verify(userService).getUserById("user1");
//    }
//
//    @Test
//    void createUser_WithValidData_ShouldCreateUser() throws Exception {
//        // Given
//        when(userService.createUser(any(User.class))).thenReturn(testUser);
//
//        // When & Then
//        mockMvc.perform(post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(testUser)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.name").value("John Doe"));
//
//        verify(userService).createUser(any(User.class));
//    }
//
//    @Test
//    void createUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
//        // Given
//        User invalidUser = new User("user2", "Jane Doe", "invalid-email", 500.0);
//
//        // When & Then
//        mockMvc.perform(post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidUser)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void updateBalance_WithValidData_ShouldUpdateBalance() throws Exception {
//        // Given
//        doNothing().when(userService).updateBalance("user1", 1500.0);
//
//        // When & Then
//        mockMvc.perform(put("/api/users/user1/balance")
//                        .param("balance", "1500.0"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Balance updated successfully"));
//
//        verify(userService).updateBalance("user1", 1500.0);
//    }
//}