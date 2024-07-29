package com.example.userservice.controller;

import com.example.userservice.user.controller.UserController;
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
    private UserService userService; // Mock UserService

    @InjectMocks
    private UserController userController; // Inject mock into UserController

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks before each test
    }

    // Test for creating a new user successfully
    @Test
    void testCreateUserSuccess() {
        Users user = new Users();
        user.setEmail("example@email.com"); // Set up user with an email

        when(userService.createUser(any(Users.class))).thenReturn("The user was created successfully"); // Mock service call

        ResponseEntity<String> response = userController.createUser(user); // Call controller method

        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // Verify status code
        assertEquals("The user was created successfully", response.getBody()); // Verify response body
    }

    // Test for creating a new user failure due to email already existing
    @Test
    void testCreateUserFailure() {
        Users user = new Users();
        user.setEmail("example@email.com"); // Set up user with an email

        doThrow(new IllegalStateException("User already exists")).when(userService).createUser(any(Users.class)); // Mock service call

        ResponseEntity<String> response = userController.createUser(user); // Call controller method

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Verify status code
        assertEquals("User already exists", response.getBody()); // Verify response body
    }

    // Test for retrieving a user by email successfully
    @Test
    void testGetUserByEmailSuccess() {
        Users user = new Users();
        user.setEmail("degoisb@email.com"); // Set up user with an email
        user.setFirstName("Bruno"); // Set first name
        user.setLastName("De Gois"); // Set last name
        user.setPassword("somePassword1"); // Set password

        when(userService.getUserByEmail("degoisb@email.com", "User with email degoisb@email.com does not exist"))
                .thenReturn(user); // Mock service call

        ResponseEntity<Object> response = userController.getUserByEmail("degoisb@email.com"); // Call controller method

        assertEquals(HttpStatus.OK, response.getStatusCode()); // Verify status code
        assertEquals(user, response.getBody()); // Verify response body
    }

    // Test for retrieving a user by email when user is not found
    @Test
    void testGetUserByEmailNotFound() {
        when(userService.getUserByEmail("example@email.com", "User with email example@email.com does not exist"))
                .thenThrow(new IllegalStateException("User with email example@email.com does not exist")); // Mock service call

        ResponseEntity<Object> response = userController.getUserByEmail("example@email.com"); // Call controller method

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // Verify status code
        assertEquals("User with email example@email.com does not exist", response.getBody()); // Verify response body
    }

    // Test for retrieving a user by email with invalid format
    @Test
    void testGetUserByEmailBadRequest() {
        when(userService.getUserByEmail("example@email.com", "User with email example@email.com does not exist"))
                .thenThrow(new IllegalStateException("Invalid email format")); // Mock service call

        ResponseEntity<Object> response = userController.getUserByEmail("example@email.com"); // Call controller method

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Verify status code
        assertEquals("Invalid email format", response.getBody()); // Verify response body
    }

    // Test for updating a user successfully
    @Test
    void testUpdateUserSuccess() {
        Users updatedUser = new Users();
        updatedUser.setEmail("example@email.com"); // Set up updated user with an email

        doNothing().when(userService).updateUser(anyString(), any(Users.class)); // Mock service call

        ResponseEntity<String> response = userController.updateUser("example@email.com", updatedUser); // Call controller method

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Verify status code
        assertEquals(null, response.getBody()); // Verify response body is null
    }

    // Test for updating a user when user is not found
    @Test
    void testUpdateUserNotFound() {
        Users updatedUser = new Users();
        updatedUser.setEmail("example@email.com"); // Set up updated user with an email

        doThrow(new IllegalStateException("User not found")).when(userService).updateUser(anyString(), any(Users.class)); // Mock service call

        ResponseEntity<String> response = userController.updateUser("example@email.com", updatedUser); // Call controller method

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // Verify status code
        assertEquals("User not found", response.getBody()); // Verify response body
    }

    // Test for updating a user with invalid request
    @Test
    void testUpdateUserBadRequest() {
        Users updatedUser = new Users();
        updatedUser.setEmail("example@email.com"); // Set up updated user with an email

        doThrow(new IllegalArgumentException("Invalid update request")).when(userService).updateUser(anyString(), any(Users.class)); // Mock service call

        ResponseEntity<String> response = userController.updateUser("example@email.com", updatedUser); // Call controller method

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Verify status code
        assertEquals("Invalid update request", response.getBody()); // Verify response body
    }

    // Test for deleting a user successfully
    @Test
    void testDeleteUserSuccess() {
        doNothing().when(userService).deleteUser("example@email.com"); // Mock service call

        ResponseEntity<String> response = userController.deleteUser("example@email.com"); // Call controller method

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Verify status code
        assertEquals(null, response.getBody()); // Verify response body is null
    }

    // Test for deleting a user when user is not found
    @Test
    void testDeleteUserNotFound() {
        doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser("example@email.com"); // Mock service call

        ResponseEntity<String> response = userController.deleteUser("example@email.com"); // Call controller method

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // Verify status code
        assertEquals("User not found", response.getBody()); // Verify response body
    }
}
