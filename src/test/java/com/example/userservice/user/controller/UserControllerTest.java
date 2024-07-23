package com.example.userservice.user.controller;

import com.example.userservice.user.model.Users;
import com.example.userservice.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for creating a new user
    @Test
    void testCreateUserSuccess() {
        Users user = new Users();
        user.setEmail("example@email.com");

        doNothing().when(userService).createUser(any(Users.class));

        ResponseEntity<String> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("The user was created successfully", response.getBody());
    }

    @Test
    void testCreateUserFailure() {
        Users user = new Users();
        user.setEmail("example@email.com");

        doThrow(new IllegalStateException("User already exists")).when(userService).createUser(any(Users.class));

        ResponseEntity<String> response = userController.createUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    // Test for retrieving a user by email
    @Test
    void testGetUserByEmailSuccess() {
        Users user = new Users();
        user.setEmail("degoisb@email.com");
        user.setFirstName("Bruno");
        user.setLastName("De Gois");
        user.setPassword("somePassword1");

        when(userService.getUserByEmail("degoisb@email.com")).thenReturn(user);

        ResponseEntity<Users> response = userController.getUserByEmail("degoisb@email.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserByEmailNotFound() {
        when(userService.getUserByEmail("example@email.com")).thenThrow(new IllegalStateException("User does not exist"));

        ResponseEntity<Users> response = userController.getUserByEmail("example@email.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testGetUserByEmailBadRequest() {
        when(userService.getUserByEmail("example@email.com")).thenThrow(new IllegalStateException("Invalid email format"));

        ResponseEntity<Users> response = userController.getUserByEmail("example@email.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    // Test for updating a user
    @Test
    void testUpdateUserSuccess() {
        Users updatedUser = new Users();
        updatedUser.setEmail("example@email.com");

        doNothing().when(userService).updateUser(anyString(), any(Users.class));

        ResponseEntity<String> response = userController.updateUser("example@email.com", updatedUser);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testUpdateUserNotFound() {
        Users updatedUser = new Users();
        updatedUser.setEmail("example@email.com");

        doThrow(new IllegalArgumentException("User not found")).when(userService).updateUser(anyString(), any(Users.class));

        ResponseEntity<String> response = userController.updateUser("example@email.com", updatedUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testUpdateUserBadRequest() {
        Users updatedUser = new Users();
        updatedUser.setEmail("example@email.com");

        doThrow(new IllegalArgumentException("Invalid update request")).when(userService).updateUser(anyString(), any(Users.class));

        ResponseEntity<String> response = userController.updateUser("example@email.com", updatedUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid update request", response.getBody());
    }

    // Test for deleting a user
    @Test
    void testDeleteUserSuccess() {
        doNothing().when(userService).deleteUser("example@email.com");

        ResponseEntity<String> response = userController.deleteUser("example@email.com");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testDeleteUserNotFound() {
        doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser("example@email.com");

        ResponseEntity<String> response = userController.deleteUser("example@email.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }
}
