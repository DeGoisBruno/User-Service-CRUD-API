package com.example.userservice.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "`user`")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "First name is mandatory")
    @NotEmpty(message = "First name is mandatory")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "First name must contain only letters")
    @Size(min = 2, max = 50, message = "First name must be between {min} and {max} characters long")
    private String firstName;

    @NotNull(message = "Last name is mandatory")
    @NotEmpty(message = "Last name is mandatory")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Last name must contain only letters")
    @Size(min = 2, max = 50, message = "Last name must be between {min} and {max} characters long")
    private String lastName;

    @NotNull(message = "Email is mandatory")
    @NotEmpty(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than {max} characters long")
    private String email;

    @NotEmpty(message = "Password is mandatory")
    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters long")
    private String password;

    public User() {
    }

    // Constructor
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
