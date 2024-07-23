package com.example.userservice.service;

import com.example.userservice.user.model.Users;
import com.example.userservice.user.repository.UserRepository;
import com.example.userservice.user.service.UserService;
import jakarta.validation.Validator;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService underTest;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<Users> userArgumentCaptor;

    // This is new
    private Validator validator;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository);
    }

    @Test
    void itShouldFindAUserByEmail() {
        // GIVEN
        // Prepare test data: email and a User object
        String email = "example@email.com";
        Users users = new Users(
                "Mike",
                "Myers",
                email,
                "somePassword");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(users));

        // WHEN
        // Call the method under test to retrieve a user by email
        Users foundUsers = underTest.getUserByEmail(email);

        // THEN
        // Assert that the retrieved user matches the expected user
        assertThat(foundUsers).isEqualTo(users);
        // Verify that userRepository.findByEmail() was called with the correct email
        verify(userRepository).findByEmail(email);
    }

    @Test
    void willThrowWhenUserNotFound() {
        // GIVEN
        // Prepare test data email with no matching user
        String email = "example@email.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // WHEN
        // THEN
        // Assert that calling getUserByEmail() with a non-existent email throws IllegalStateException
        assertThatThrownBy(() -> underTest.getUserByEmail(email))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User with email " + email + " does not exist");

        // Verify that userRepository.findByEmail() was called with the correct email
        verify(userRepository).findByEmail(email);
    }

    @Test
    void itShouldCreateANewUser() throws BadRequestException {
        // GIVEN
        Users users = new Users(
                "Mike",
                "Myers",
                "example@email.com",
                "somePassword1");

        given(userRepository.existsByEmail(users.getEmail())).willReturn(false); // Mock repository response

        // WHEN
        underTest.createUser(users);

        // THEN
        verify(userRepository).save(any(Users.class)); // Verify userRepository.save() was called
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // GIVEN
        String existingEmail = "example@email.com";
        Users users = new Users(
                "Mike",
                "Myers",
                existingEmail,
                "somePassword1");

        given(userRepository.existsByEmail(existingEmail)).willReturn(true); // Mock repository response

        // THEN
        assertThatThrownBy(() -> underTest.createUser(users))
                .isInstanceOf(IllegalStateException.class) // Expect BadRequestException
                .hasMessage("Email already exists"); // Verify exception message
    }

    @Test
    void createNewUser_whenEmailNotProvided_shouldThrowIllegalStateException() {
        // GIVEN
        Users users = new Users(
                "Mike",
                "Myers",
                null,
                "somePassword1");

        // WHEN & THEN
        assertThatThrownBy(() -> userService.createUser(users))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email is required");

        // Verify no interactions with userRepository
        verifyNoInteractions(userRepository);
    }

    @Test
    void createNewUser_whenPasswordNotProvided_shouldThrowIllegalArgumentException() {
        // GIVEN
        Users users = new Users(
                "Mike",
                "Myers",
                "example@email.com",
                null);

        // WHEN & THEN
        assertThatThrownBy(() -> userService.createUser(users))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Password is required");

        verifyNoInteractions(userRepository);
    }

    @Test
    void createNewUser_whenPasswordTooShort_shouldThrowIllegalArgumentException() {
        // GIVEN
        Users users = new Users(
                "Mike",
                "Myers",
                "example@email.com",
                "short");

        // WHEN & THEN
        assertThatThrownBy(() -> userService.createUser(users))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Password must be between 8 and 20 characters long");

        verifyNoInteractions(userRepository);
    }

    @Test
    void createNewUser_whenPasswordTooLong_shouldThrowIllegalArgumentException() {
        // GIVEN
        Users users = new Users(
                "Mike",
                "Myers",
                "example@email.com",
                "thispasswordiswaytoolongtobeaccepted");

        // WHEN & THEN
        assertThatThrownBy(() -> userService.createUser(users))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Password must be between 8 and 20 characters long");

        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUser_UserNotFound() {
        // GIVEN
        String email = "example@email.com";
        Users updatedUser = new Users("Mike", "Myers", email, "newPassword");
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");

        // Verify userRepository.findByEmail() was called
        verify(userRepository).findByEmail(email);
        // Verify userRepository.save() was never called
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void updateUser_InvalidEmailFormat() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("Mike", "Myers", email, "password123");
        Users updatedUser = new Users("Mike", "Myers", "invalid-email", "password123");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");

        // Verify userRepository.findByEmail() was called
        verify(userRepository).findByEmail(email);
        // Verify userRepository.save() was never called
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void updateUser_EmailAlreadyExists() {
        // GIVEN
        String email = "example@email.com";
        String newEmail = "new@example.com";
        Users existingUser = new Users("Mike", "Myers", email, "password123");
        Users updatedUser = new Users("Mike", "Myers", newEmail, "password123");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));
        given(userRepository.existsByEmail(newEmail)).willReturn(true);

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");

        // Verify userRepository.findByEmail() was called
        verify(userRepository).findByEmail(email);
        // Verify userRepository.existsByEmail() was called
        verify(userRepository).existsByEmail(newEmail);
        // Verify userRepository.save() was never called
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void updateUser_PasswordTooShort() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("Mike", "Myers", email, "password123");
        Users updatedUser = new Users("Mike", "Myers", email, "short");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password must be between 8 and 20 characters");

        // Verify userRepository.findByEmail() was called
        verify(userRepository).findByEmail(email);
        // Verify userRepository.save() was never called
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void updateUser_PasswordTooLong() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("Mike", "Myers", email, "password123");
        Users updatedUser = new Users("Mike", "Myers", email, "thispasswordistoolongtobevalid");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password must be between 8 and 20 characters");

        // Verify userRepository.findByEmail() was called
        verify(userRepository).findByEmail(email);
        // Verify userRepository.save() was never called
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void updateUser_FirstNameInvalid() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("Mike", "Myers", email, "password123");
        Users updatedUser = new Users("M1ke", "Myers", email, "password123");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("First name can only contain letters and spaces");

        // Verify userRepository.findByEmail() was called
        verify(userRepository).findByEmail(email);
        // Verify userRepository.save() was never called
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void updateUser_LastNameInvalid() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("Mike", "Myers", email, "password123");
        Users updatedUser = new Users("Mike", "My3rs", email, "password123");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Last name can only contain letters and spaces");

        // Verify userRepository.findByEmail() was called
        verify(userRepository).findByEmail(email);
        // Verify userRepository.save() was never called
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void updateUser_NoFieldsUpdated() {
        // GIVEN
//        String email = "example@email.com";
        Users existingUser = new Users("", "", "", "");
        Users updatedUser = new Users("", "", "", ""); // Same data

        // Mock repository behavior
        given(userRepository.findByEmail("")).willReturn(Optional.of(existingUser));

        // WHEN & THEN
        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            underTest.updateUser("", updatedUser);
        });

        // Verify the exception message
        assertEquals("No fields updated", thrownException.getMessage());

        // Verify userRepository.findByEmail() was called
        verify(userRepository).findByEmail("");

        // Verify userRepository.save() was never called
        verify(userRepository, never()).save(any(Users.class));
    }


    @Test
    void updateUser_Success() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("Mike", "Myers", email, "password123");
        Users updatedUser = new Users("Michael", "Meyers", "new@example.com", "newPassword123");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));
        given(userRepository.existsByEmail("new@example.com")).willReturn(false);

        // WHEN
        userService.updateUser(email, updatedUser);

        // THEN
        verify(userRepository).findByEmail(email);
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteUserNotFound() {
        // GIVEN
        String email = "example@email.com";
        // Mocking scenario where user does not exist
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // WHEN and THEN (using AssertJ for fluent assertion)
        assertThatThrownBy(() -> userService.deleteUser(email))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User with email " + email + " not found");

        // Verify that userRepository.findByEmail() was called with the correct email
        verify(userRepository).findByEmail(email);
        // Verify that userRepository.delete() was never called
        verify(userRepository, never()).delete(any(Users.class));
    }

    @Test
    void deleteUserSuccess() {
        // GIVEN
        String email = "example@email.com";
        Users user = new Users(
                "Mike",
                "Myers",
                email,
                "somePassword1");
        // Mocking scenario where user exists
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // WHEN
        userService.deleteUser(email);

        // THEN
        // Verify that userRepository.findByEmail() was called with the correct email
        verify(userRepository).findByEmail(email);
        // Verify that userRepository.delete() was called with the correct user
        verify(userRepository).delete(user);
    }
}