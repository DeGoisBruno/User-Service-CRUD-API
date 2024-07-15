package com.example.userservice.user.controller;

import com.example.userservice.user.model.User;
import com.example.userservice.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testGetUser() throws Exception {
        // Mock data
        User mockUser = new User("John", "Doe", "john.doe@example.com", "password");

        when(userService.getUser()).thenReturn(Collections.singletonList(mockUser));

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/userservice/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(mockUser.getEmail()));
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        // Mock data
        String userEmail = "john.doe@example.com";
        User mockUser = new User("John", "Doe", userEmail, "password");

        when(userService.getUserByEmail(anyString())).thenReturn(mockUser);

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/userservice/user")
                        .param("email", userEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(mockUser.getEmail()));
    }

    @Test
    public void testUpdateUser_ValidInput_ReturnsOk() throws Exception {
        // Mock input
        String email = "test@example.com";
        String password = "password";
        String newEmail = "new@example.com";
        String newPassword = "newpassword";
        String confirmNewPassword = "newpassword";

        // Mock userService behavior
        doNothing().when(userService).updateUser(email, password, newEmail, newPassword, confirmNewPassword);

        // Perform PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/userservice/update-user")
                        .param("email", email)
                        .param("password", password)
                        .param("newEmail", newEmail)
                        .param("newPassword", newPassword)
                        .param("confirmNewPassword", confirmNewPassword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User updated successfully"));

        // Verify userService method invocation
        verify(userService, times(1)).updateUser(email, password, newEmail, newPassword, confirmNewPassword);
    }

    @Test
    public void testDeleteUser() throws Exception {
        // Mock input
        String email = "test@example.com";

        // Mock userService behavior
        doNothing().when(userService).deleteUser(email);

        // Perform DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/userservice/delete")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("The user was successfully deleted"));

        // Verify userService method invocation
        verify(userService, times(1)).deleteUser(email);
    }

    // Helper method to convert object to JSON string
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
