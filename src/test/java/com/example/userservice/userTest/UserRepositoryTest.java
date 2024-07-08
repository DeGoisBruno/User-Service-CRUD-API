package com.example.userservice.userTest;

import com.example.userservice.user.model.User;
import com.example.userservice.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

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
        User user = new User(
                "Mike",
                "Myers",
                email,
                "password"
        );
        underTest.save(user);

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