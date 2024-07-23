package com.example.userservice.repository;

import com.example.userservice.user.model.Users;
import com.example.userservice.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UsersRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldCheckIfEmailExists() {
        // GIVEN
        String email = "example@email.com";
        Users users = new Users(
                "Mike",
                "Myers",
                email,
                "password"
        );
        underTest.save(users);

        // WHEN
        boolean exists = underTest.existsByEmail(email);

        // THEN
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldCheckIfEmailDoesNotExist() {
        // GIVEN
        String email = "example@email.com";

        // WHEN
        boolean exists = underTest.existsByEmail(email);

        // THEN
        assertThat(exists).isFalse();
    }
}