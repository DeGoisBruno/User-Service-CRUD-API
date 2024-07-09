package com.example.userservice.user.controller;

import com.example.userservice.user.model.User;
import com.example.userservice.user.service.UserService;
import jakarta.websocket.server.PathParam;
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
                @PathParam("email") String email) {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        }

        // Create new user
        @PostMapping("/register")
        public ResponseEntity<String> registerUser(@RequestBody User user) {
            try {
                userService.addNewUser(user);
                return ResponseEntity.status(HttpStatus.CREATED).body("The user was created successfully");
            } catch (IllegalStateException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

    // Update a user by email
    @PutMapping(path = "/update")
    public ResponseEntity<String> updateUser(
            @RequestParam("email") String email,
            @RequestBody User updatedUser) {
        userService.updateUser(email, updatedUser);
        return ResponseEntity.ok("The user was updated successfully");
    }

    // Delete an user by email
        @DeleteMapping(path = "/delete")
        public ResponseEntity<String> deleteUser(
                @PathParam("email") String email) {
            userService.deleteUser(email);
            return ResponseEntity.ok("The user was successfully deleted");
        }
    }
