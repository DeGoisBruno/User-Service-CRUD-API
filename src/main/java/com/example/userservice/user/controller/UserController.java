package com.example.userservice.user.controller;

import com.example.userservice.user.dto.UserDTO;
import com.example.userservice.user.model.User;
import com.example.userservice.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/userservice")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Retrieves all users
    @GetMapping("/list")
    public List<User> getUser() {
        return userService.getUser();
    }

    // Get a user by email
    @GetMapping("/user")
    public ResponseEntity<User> getUserByEmail(
            @RequestParam("email") String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    // Create a new user and add to the repository
    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            userService.createUser(userDTO);
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    // Update user email or password
    @PutMapping("/update-user")
    public ResponseEntity<String> updateUser(
            @RequestParam String email, // Current email of the user (required)
            @RequestParam String password, // Current password of the user (required)
            @RequestParam(required = false) String newEmail, // New email to update (optional)
            @RequestParam(required = false) String newPassword, // New password to update (optional)
            @RequestParam(required = false) String confirmNewPassword) { // Confirmation for the new password (optional)
        try {
            userService.updateUser(email, password, newEmail, newPassword, confirmNewPassword);
            return ResponseEntity.ok("User updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    // Delete a user by email
    @DeleteMapping(path = "/delete")
    public ResponseEntity<String> deleteUser(
            @RequestParam("email") String email) {
        try {
            userService.deleteUser(email);
            return ResponseEntity.ok("The user was successfully deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }
}