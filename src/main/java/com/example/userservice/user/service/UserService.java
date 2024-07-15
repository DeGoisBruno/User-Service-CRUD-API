package com.example.userservice.user.service;

import com.example.userservice.user.dto.UserDTO;
import com.example.userservice.user.model.User;
import com.example.userservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Retrieves all users from the repository
    public List<User> getUser() {
        return userRepository.findAll();
    }

    // Retrieves a user from the repository by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with email " + email + " does not exist"));
    }


// Create a new user and add to the repository
public void createUser(UserDTO userDTO) throws IllegalStateException {
    if (userDTO == null || userDTO.getEmail() == null || userDTO.getPassword() == null) {
        throw new IllegalStateException("Name or email or password cannot be null");
    }

    if (userRepository.existsByEmail(userDTO.getEmail())) {
        throw new IllegalStateException("Email already exists");
    }

    User user = new User(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getPassword()  // No password encoding for illustration
    );

    userRepository.save(user);
}


    // Update a user password or email
    @Transactional
    public void updateUser(String email, String password, String newEmail, String newPassword, String confirmNewPassword) throws IllegalStateException {
        // Retrieve the user from the database based on the provided email
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Verify if the provided password matches the user's current password
        if (!existingUser.getPassword().equals(password)) {
            throw new IllegalStateException("Incorrect password");
        }

        // Flags to track if email or password has been changed
        boolean emailChanged = false;
        boolean passwordChanged = false;

        // Check and update email if newEmail is provided and different from the current email
        if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(existingUser.getEmail())) {
            // Check if the new email already exists in the database
            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalStateException("Email already exists");
            }
            existingUser.setEmail(newEmail);
            emailChanged = true; // Mark email as changed
        }

        // Check and update password if newPassword is provided
        if (newPassword != null && !newPassword.isEmpty()) {
            // Verify if newPassword matches the confirmNewPassword
            if (!newPassword.equals(confirmNewPassword)) {
                throw new IllegalStateException("New passwords do not match");
            }

            // Validate new password length
            if (newPassword.length() < 6 || newPassword.length() > 15) {
                throw new IllegalStateException("Password must be between 6 and 15 characters long");
            }

            existingUser.setPassword(newPassword);
            passwordChanged = true; // Mark password as changed
        }

        // If neither email nor password was changed, throw an exception
        if (!emailChanged && !passwordChanged) {
            throw new IllegalStateException("No fields updated");
        }

        // Save the updated user entity to the database
        userRepository.save(existingUser);
    }


    // Delete a user from the repository by email
    public void deleteUser(String email) {
        boolean exists = userRepository.existsByEmail(email);
        if (!exists) {
            throw new IllegalStateException("User with email " + email + " does not exist");
        }
        userRepository.deleteByEmail(email);
    }
}
