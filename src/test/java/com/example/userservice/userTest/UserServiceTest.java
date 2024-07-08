package com.example.userservice.userTest;

import com.example.userservice.user.model.User;
import com.example.userservice.user.repository.UserRepository;
import com.example.userservice.user.service.UserService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService underTest;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void getUser() {
        // WHEN
        // Call the method under test to retrieve all users
        underTest.getUser();

        // THEN
        // Verify that userRepository.findAll() was called
        verify(userRepository).findAll();
    }

    @Test
    void getUserByEmail() {
        // GIVEN
        // Prepare test data: email and a User object
        String email = "example@email.com";
        User user = new User(
                "Mike",
                "Myers",
                email,
                "somePassword");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // WHEN
        // Call the method under test to retrieve a user by email
        User foundUser = underTest.getUserByEmail(email);

        // THEN
        // Assert that the retrieved user matches the expected user
        assertThat(foundUser).isEqualTo(user);
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
    void addNewUser() throws BadRequestException {
        // GIVEN
        // Create a new User object with specific details
        User user = new User(
                "Mike",
                "Myers",
                "example@email.com",
                "somePassword"
        );

        // WHEN
        // Call the method under test to add the new user
        underTest.addNewUser(user);

        // THEN
        // Verify that userRepository.save() was called with the expected User object
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        // Retrieve the captured User object and assert its equality to the original user object
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    public void willThrowWhenEmailIsTaken() {
        // GIVEN
        // Mocking a scenario where userRepository.findByEmail() returns a non-empty Optional
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(new User(
                        "Mike",
                        "Myers",
                        "example@email.com",
                        "somePassword")));

        // WHEN
        // Creating a new user attempt with the same email
        User newUser = new User(
                "Mike",
                "Myers",
                "example@email.com",
                "somePassword");

        // THEN
        // Expecting an IllegalStateException when attempting to add a user with a duplicate email
        assertThrows(IllegalStateException.class, () -> userService.addNewUser(newUser));
    }

    @Test
    void addNewUser_whenEmailNotProvided_shouldThrowIllegalArgumentException() {
        // GIVEN
        User user = new User(
                "Mike",
                "Myers",
                null,  // No email provided
                "somePassword"
        );

        // WHEN
        // Invoke the method under test and expect an exception
        // THEN
        assertThatThrownBy(() -> underTest.addNewUser(user))
                // Ensure that the exception thrown is of type IllegalArgumentException
                .isInstanceOf(IllegalArgumentException.class)
                // Check that the exception message matches the expected message
                .hasMessage("Email is required");

        // Verify that userRepository methods were not called
        verifyNoInteractions(userRepository);
    }

    @Test
    void addNewUser_whenPasswordNotProvided_shouldThrowIllegalArgumentException() {
        // GIVEN
        User user = new User(
                "Mike",
                "Myers",
                "example@email.com",
                null  // No password provided
        );

        // WHEN
        // Invoke the method under test and expect an exception
        // THEN
        assertThatThrownBy(() -> underTest.addNewUser(user))
                // Ensure that the exception thrown is of type IllegalArgumentException
                .isInstanceOf(IllegalArgumentException.class)
                // Check that the exception message matches the expected message
                .hasMessage("Password is required");

        // Verify that userRepository methods were not called
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUser_whenUserExists_shouldUpdateUserDetails() {
        // GIVEN
        String email = "john.doe@example.com";
        User existingUser = new User(
                "Mike",
                "Myers",
                email,
                "oldPassword"
        );
        User updatedUser = new User(
                "Eddie",
                "Murphy",
                email,
                "newPassword"
        );

        // // Mocking scenario where userRepository.findByEmail() returns an Optional containing existingUser
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // WHEN
        underTest.updateUser(email, updatedUser);

        // THEN
        // Verify that userRepository.findByEmail() was called with the correct email
        verify(userRepository).findByEmail(email);
        // Verify that userRepository.save() was called with existingUser to update details
        verify(userRepository).save(existingUser);
        // Assert that the updated details in existingUser match updatedUser
        assertThat(existingUser.getFirstName()).isEqualTo(updatedUser.getFirstName());
        assertThat(existingUser.getLastName()).isEqualTo(updatedUser.getLastName());
        assertThat(existingUser.getEmail()).isEqualTo(updatedUser.getEmail());
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldThrowIllegalStateException() {
        // GIVEN
        String email = "nonexistentemail@example.com";
        User updatedUser = new User(
                "Eddie",
                "Murphy",
                email,
                "newPassword"
        );

        // Mocking scenario where userRepository.findByEmail() returns an empty Optional
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // WHEN
        // THEN
        assertThatThrownBy(() -> underTest.updateUser(email, updatedUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User with email " + email + " does not exist");

        // Verify that userRepository.findByEmail() was called with the correct email
        verify(userRepository).findByEmail(email);
        // Verify no other interactions with userRepository
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser() {
        // GIVEN
        String email = "example@email.com";
        // Mocking scenario where user exists
        given(userRepository.existsByEmail(email)).willReturn(true);

        // WHEN
        underTest.deleteUser(email);

        // THEN
        // Verify that userRepository.deleteByEmail() was called with the correct email
        verify(userRepository).deleteByEmail(email);
    }

    @Test
    void deleteUserNotFound() {
        // GIVEN
        String email = "example@email.com";
        // Mocking scenario where user does not exist
        given(userRepository.existsByEmail(email)).willReturn(false);

        // WHEN and THEN (using AssertJ for fluent assertion)
        assertThatThrownBy(() -> underTest.deleteUser(email))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User with email " + email + " does not exist");

        // Verify that userRepository.existsByEmail() was called with the correct email
        verify(userRepository).existsByEmail(email);
        // Verify that userRepository.deleteByEmail() was never called
        verify(userRepository, never()).deleteByEmail(email);
    }
}