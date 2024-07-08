package com.example.userservice.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
                return new OpenAPI()
                    .info(new io.swagger.v3.oas.models.info.Info()
                            .title("User Service System API")
                            .version("1.0")
                            .description("API documentation for a service for creating, retrieving, updating, and deleting users."));
    }
}