package com.example.userservice.user.config;

import com.example.userservice.user.model.Users;
import com.example.userservice.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserConfig {
    @Bean
    CommandLineRunner commandLineRunner(UserRepository repository) {
        return  args -> {
            Users users1 = new Users(
                    "Thomas",
                    "Hedlund",
                    "hedlund.thomas@email.com",
                    "somePassword1"
            );
            Users users2 = new Users(
                    "Julie",
                    "Christmas",
                    "juliexmas@email.com",
                    "somePassword2"
            );

            // Save users to the repository
            repository.saveAll(List.of(users1, users2));
        };
    }
}
