package com.example.userservice.user.repository;

import com.example.userservice.user.model.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    // Find a user by email
    Optional<Users> findByEmail(String email);

    // Check if a user exists by email
    boolean existsByEmail(String email);

    // Delete a user by email
    @Modifying
    @Transactional
    void deleteByEmail(String email);
}