package com.example.userservice.user.service;

import com.example.userservice.user.model.User;
import com.example.userservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Retrieves all users from the repository
    public List<User> getUser() {
        return userRepository.findAll();
    }

    // Retrieves a user from the repository by email
    @Transactional
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with email " + email + " does not exist"));
    }

    // Add a new user to the repository
    public void addNewUser(User user) {
        // Check if email and password are provided
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Check if the user already exists
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            throw new IllegalStateException("User with email " + user.getEmail() + " already exists");
        }

        // Encode the password and save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

// Updates user details
@Transactional
public void updateUser(String email, User updatedUser) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("User with email " + email + " does not exist"));

    if (updatedUser.getFirstName() != null) {
        user.setFirstName(updatedUser.getFirstName());
    }

    if (updatedUser.getLastName() != null) {
        user.setLastName(updatedUser.getLastName());
    }

    if (updatedUser.getEmail() != null) {
        user.setEmail(updatedUser.getEmail());
    }

    userRepository.save(user);
}

    // Deletes a user from the repository by email
    @Transactional
    public void deleteUser(String email) {
        boolean exists = userRepository.existsByEmail(email);
        if (!exists) {
            throw new IllegalStateException("User with email " + email + " does not exist");
        }
        userRepository.deleteByEmail(email);
    }
}