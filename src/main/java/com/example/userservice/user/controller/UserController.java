package com.example.userservice.user.controller;

import com.example.userservice.user.model.Users;
import com.example.userservice.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/userservice")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    // Create a new user
    @Operation(summary = "Creates a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The user was created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/users")
    public ResponseEntity<String> createUser(
            @Valid @RequestBody Users user) {
        try {
            String message = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(message); // 201 Successfully created
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad request
        }
    }


    // Get a user by email
    @Operation(summary = "Retrieve an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "A user with the specified email does not exist")
    })
    @GetMapping("/users/{email}")
    public ResponseEntity<Object> getUserByEmail(
            @Parameter(name = "email", description = "User email", required = true, in = ParameterIn.PATH)
            @PathVariable("email") String email) {
        try {
            Users user = userService.getUserByEmail(email, "User with email " + email + " does not exist");
            return ResponseEntity.ok(user); // 200 User found
        } catch (IllegalStateException e) {
            if (e.getMessage().contains(email)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request
        }
    }


    // Update user email or password
    @Operation(summary = "Update an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The user was updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "A user with the specified email does not exist")
    })
    @PutMapping("/users/{email}")
    public ResponseEntity<String> updateUser(
            @Parameter(name = "email", description = "User email", required = true, in = ParameterIn.PATH)
            @PathVariable String email,
            @Valid @RequestBody Users updatedUser) {
        try {
            userService.updateUser(email, updatedUser);
            return ResponseEntity.noContent().build(); // 204 Update was successful
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request for all validation errors
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        }
    }


    // Delete a user by email
    @Operation(summary = "Delete an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The user was deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @DeleteMapping("/users/{email}")
    public ResponseEntity<String> deleteUser(
            @Parameter(name = "email", description = "User email", required = true, in = ParameterIn.PATH)
            @PathVariable String email) {
        try {
            userService.deleteUser(email);
            return ResponseEntity.noContent().build(); // 204 Successfully deleted
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 400 Bad request
        }
    }
}
