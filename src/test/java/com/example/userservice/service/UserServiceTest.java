package com.example.userservice.service;

import com.example.userservice.user.dto.UserDTO;
import com.example.userservice.user.model.User;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService underTest;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    // This is new
    private Validator validator;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository);
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
    void itShouldCreateANewUser() throws BadRequestException {
        // GIVEN
        UserDTO userDTO = new UserDTO("Mike",
                "Myers",
                "example@email.com",
                "somePassword1",
                "somePassword1");

        given(userRepository.existsByEmail(userDTO.getEmail())).willReturn(false); // Mock repository response

        // WHEN
        underTest.createUser(userDTO);

        // THEN
        verify(userRepository).save(any(User.class)); // Verify userRepository.save() was called
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // GIVEN
        String existingEmail = "example@email.com";
        UserDTO userDTO = new UserDTO("Mike",
                "Myers",
                "example@email.com",
                "somePassword",
                "somePassword");
        given(userRepository.existsByEmail(existingEmail)).willReturn(true); // Mock repository response

        // THEN
        assertThatThrownBy(() -> underTest.createUser(userDTO))
                .isInstanceOf(IllegalStateException.class) // Expect BadRequestException
                .hasMessage("Email already exists"); // Verify exception message
    }

    @Test
    void createNewUser_whenEmailNotProvided_shouldThrowIllegalStateException() {
        // GIVEN
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = new UserService(userRepository);

        // Create a UserDTO with null email
        UserDTO userDTO = new UserDTO("Mike",
                "Myers",
                null,
                "somePassword",
                "somePassword");

        // WHEN & THEN
        assertThatThrownBy(() -> userService.createUser(userDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Name or email or password cannot be null");

        // Verify no interactions with userRepository
        verifyNoInteractions(userRepository);
    }


    @Test
    void createNewUser_whenPasswordNotProvided_shouldThrowIllegalArgumentException() {
            // GIVEN
            UserDTO userDTO = new UserDTO("Mike",
                    "Myers",
                    "example@email.com",
                    null,
                    null);

            // WHEN & THEN
            assertThatThrownBy(() -> underTest.createUser(userDTO))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Name or email or password cannot be null");

            // Verify that userRepository methods were not called
            verifyNoInteractions(userRepository);
        }


        @Test
        void updateUser_whenUserExists_updateOnlyEmail_shouldUpdateEmail() {
        // GIVEN
        String email = "leslie.nielsen@example.com";
        String newPassword = "newPassword";
        String confirmNewPassword = "newPassword";
        UserDTO userDTO = new UserDTO("Leslie",
                "Nielsen",
                "newemail@example.com",
                newPassword,
                confirmNewPassword);

        // Mock repository response
        User existingUser = new User(userDTO.getFirstName(), userDTO.getLastName(), email, "oldPassword");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        // WHEN
        // Update the user's email
        underTest.updateUser(email, "oldPassword", userDTO.getEmail(), newPassword, confirmNewPassword);

        // THEN
        // Verify that userRepository.findByEmail() was called with the correct email
        verify(userRepository).findByEmail(email);
        // Verify that userRepository.save() was called with existingUser to update details
        verify(userRepository).save(existingUser);
        // Assert that the updated email in existingUser matches the new email in userDTO
        assertThat(existingUser.getEmail()).isEqualTo(userDTO.getEmail());
    }


    @Test
    void updateUser_whenUserDoesNotExist_shouldThrowIllegalStateException() {
        // GIVEN
        String email = "nonexistentemail@example.com";
        UserDTO userDTO = new UserDTO(
                "Eddie",
                "Murphy", email,
                "newPassword",
                "newPassword");

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // WHEN
        // THEN
        assertThatThrownBy(() -> underTest.updateUser(
                email,
                "oldPassword",
                userDTO.getFirstName(),
                userDTO.getPassword(),
                userDTO.getConfirmPassword()))
                    .isInstanceOf(IllegalStateException.class);

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