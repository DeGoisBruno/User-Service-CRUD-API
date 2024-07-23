package com.example.userservice.user.service;

import com.example.userservice.user.model.Users;
import com.example.userservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Retrieves a user from the repository by email
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with email " + email + " does not exist"));
    }


    // Create a new user and add to the repository
    @Transactional
    public void createUser(Users user) throws IllegalStateException {
        // Validate email
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalStateException("Email is required");
        }

        // Validate password
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalStateException("Password is required");
        }
        if (user.getPassword().length() < 8 || user.getPassword().length() > 20) {
            throw new IllegalStateException("Password must be between 8 and 20 characters long");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        // Save the user
        userRepository.save(user);
    }

    // Update existing user
    @Transactional
    public void updateUser(String email, Users updatedUser) {
        // Fetch the existing user by email
        Users existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isUpdated = false;

        // Validate and update email if provided and not empty
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty() &&
                !updatedUser.getEmail().equals(existingUser.getEmail())) { // Added comparison
            if (!isValidEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            existingUser.setEmail(updatedUser.getEmail());
            isUpdated = true;
        }

        // Validate and update password if provided and not empty
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            System.out.println("Password length: " + updatedUser.getPassword().length()); // Debugging line
            if (updatedUser.getPassword().length() < 8 || updatedUser.getPassword().length() > 20) {
                throw new IllegalArgumentException("Password must be between 8 and 20 characters");
            }

            existingUser.setPassword(updatedUser.getPassword());
            isUpdated = true;
        }

        // Validate and update first name if provided
        if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().isEmpty() &&
                !updatedUser.getFirstName().equals(existingUser.getFirstName())) {
            if (!updatedUser.getFirstName().matches("^[a-zA-Z\\s]+$")) { // Simple validation
                throw new IllegalArgumentException("First name can only contain letters and spaces");
            }
            existingUser.setFirstName(updatedUser.getFirstName());
            isUpdated = true;
        }

        // Validate and update last name if provided
        if (updatedUser.getLastName() != null && !updatedUser.getLastName().isEmpty() &&
                !updatedUser.getLastName().equals(existingUser.getLastName())) {
            if (!updatedUser.getLastName().matches("^[a-zA-Z\\s]+$")) { // Simple validation
                throw new IllegalArgumentException("Last name can only contain letters and spaces");
            }
            existingUser.setLastName(updatedUser.getLastName());
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new IllegalArgumentException("No fields updated");
        }

        // Save the updated user
        userRepository.save(existingUser);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }


    // Delete a user from the repository by email
    @Transactional
    public void deleteUser(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with email " + email + " not found"));

        userRepository.delete(user);
    }
}