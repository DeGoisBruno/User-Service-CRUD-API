package com.example.userservice.user.service;

import com.example.userservice.user.model.Users;
import com.example.userservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Define isValidEmail method
    public boolean isValidEmail(String email) {
        // Email validation regex
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    // Create a new user
    @Transactional
    public String createUser(Users user) {
        // Validate email
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalStateException("Email is required");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalStateException("Email should be valid");
        }

        // Validate password
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalStateException("Password is required");
        }
        if (user.getPassword().length() < 8 || user.getPassword().length() > 20) {
            throw new IllegalStateException("Password must be between 8 and 20 characters");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        // Save the user
        userRepository.save(user);

        // Return success message
        return "The user was created successfully";
    }


    // Retrieves a user from the repository by email
    public Users getUserByEmail(String email, String message) {
        String defaultMessage = message == null || message.isEmpty() ? "User with email " + email + " does not exist" : message;

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(message));
    }

    // Update a existing user
    @Transactional
    public void updateUser(String email, Users updatedUser) {
        Users existingUser = getUserByEmail(email, "User with email " + email + " does not exist");

        boolean isUpdated = false;

        // Validate and update email
        String newEmail = updatedUser.getEmail();
        if (newEmail != null && !newEmail.isEmpty()) {
            if (!newEmail.equals(existingUser.getEmail()) &&
                    userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email already exists");
            }
            existingUser.setEmail(newEmail);
            isUpdated = true;
        }

        // Validate and update password
        String newPassword = updatedUser.getPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < 8 || newPassword.length() > 20) {
                throw new IllegalArgumentException("Password must be between 8 and 20 characters");
            }
            existingUser.setPassword(newPassword);
            isUpdated = true;
        }

        // Validate and update first name
        if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().isEmpty() &&
                !updatedUser.getFirstName().equals(existingUser.getFirstName())) {
            if (updatedUser.getFirstName().length() > 50) {
                throw new IllegalArgumentException("First name must be less than 50 characters long");
            }
            existingUser.setFirstName(updatedUser.getFirstName());
            isUpdated = true;
        }

        // Validate and update last name
        if (updatedUser.getLastName() != null && !updatedUser.getLastName().isEmpty() &&
                !updatedUser.getLastName().equals(existingUser.getLastName())) {
            if (updatedUser.getLastName().length() > 100) {
                throw new IllegalArgumentException("Last name must be less than 100 characters long");
            }
            existingUser.setLastName(updatedUser.getLastName());
            isUpdated = true;
        }

        // Check if any fields were updated
        if (!isUpdated) {
            throw new IllegalArgumentException("No fields updated");
        }

        // Save the updated user
        userRepository.save(existingUser);
    }

    // Delete a user from the repository by email
    @Transactional
    public void deleteUser(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with email " + email + " does not exist"));

        userRepository.delete(user);
    }
}
