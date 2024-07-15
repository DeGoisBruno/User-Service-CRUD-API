package com.example.userservice.user.config;

import com.example.userservice.user.model.User;
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
            User user1 = new User(
                    "Thomas",
                    "Hedlund",
                    "hedlund.thomas@email.com",
                    "somePassword1"
            );
            User user2 = new User(
                    "Julie",
                    "Christmas",
                    "juliexmas@email.com",
                    "somePassword2"
            );

            // Save users to the repository
            repository.saveAll(List.of(user1,user2));
        };
    }
}
