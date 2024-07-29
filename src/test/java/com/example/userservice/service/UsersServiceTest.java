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

    //private Validator validator;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository);
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
    void testCreateUser_ValidUser() {
        // GIVEN
        // A valid user object is created with all necessary fields
        Users user = new Users();
        user.setEmail("valid@example.com");
        user.setPassword("ValidPass123");
        user.setFirstName("Bruno");
        user.setLastName("De Gois");

        // Mock repository to return false for the email existence check
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        // WHEN
        // Attempt to create the user
        String result = userService.createUser(user);

        // THEN
        assertEquals("The user was created successfully", result); // Verify that the user was created successfully

        verify(userRepository, times(1)).save(user); // Verify that the save method was called once with the correct user object
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
                .hasMessage("Password must be between 8 and 20 characters");

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
                .hasMessage("Password must be between 8 and 20 characters");

        verifyNoInteractions(userRepository);
    }

    @Test
    void testCreateUser_InvalidEmail() {
        // GIVEN: An instance of the Users class with an invalid email and a valid password.
        Users user = new Users();
        user.setEmail("invalidemail"); // Invalid email format
        user.setPassword("ValidPass123"); // Valid password

        // WHEN: The createUser method of userService is called with the user instance.
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            userService.createUser(user); // This should trigger an exception due to invalid email
        });

        // THEN: Verify that the exception thrown has the expected message.
        assertEquals("Email should be valid", thrown.getMessage()); // Check if the message matches the expected error
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // GIVEN
        String existingEmail = "example@email.com"; // Define an email that is already taken
        Users users = new Users(
                "Mike",
                "Myers",
                existingEmail,
                "somePassword1");

        // Mock the repository response to return true when checking if the email exists
        given(userRepository.existsByEmail(existingEmail)).willReturn(true); // Mock repository response

        // THEN
        // Verify that when attempting to create a user with an existing email, an IllegalStateException is thrown with the message "Email already exists"
        assertThatThrownBy(() -> underTest.createUser(users))
                .isInstanceOf(IllegalStateException.class) // Expect BadRequestException
                .hasMessage("Email already exists"); // Verify exception message
    }

    @Test
    void testCreateUser_EmptyFirstNameAndLastName() {
        // GIVEN
        // Create a new user without first name and last name
        Users user = new Users();
        user.setEmail("valid@example.com");
        user.setPassword("ValidPass123");
        user.setFirstName(""); // Empty first name
        user.setLastName(""); // Empty last name

        // Mock repository to return false for the email existence check
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        // WHEN
        String result = userService.createUser(user); // Attempt to create the user

        // THEN
        assertEquals("The user was created successfully", result); // Verify that the user was created successfully

        verify(userRepository, times(1)).save(user); // Verify that the save method was called once with the correct user object
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
        Users foundUsers = underTest.getUserByEmail(email, null); // Call the method under test to retrieve a user by email

        // THEN
        assertThat(foundUsers).isEqualTo(users); // Assert that the retrieved user matches the expected user
        verify(userRepository).findByEmail(email); // Verify that userRepository.findByEmail() was called with the correct email
    }

    @Test
    void willThrowWhenUserNotFound() {
        // GIVEN
        // Prepare test data email with no matching user
        String email = "example@email.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // WHEN & THEN
        // Assert that calling getUserByEmail() with a non-existent email throws IllegalStateException
        assertThatThrownBy(() -> underTest.getUserByEmail(email, "User with email " + email + " does not exist"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User with email " + email + " does not exist");

        verify(userRepository).findByEmail(email); // Verify that userRepository.findByEmail() was called with the correct email
    }


    // Focuses on verifying that the necessary repository methods are called during the execution
    @Test
    void updateUser_Success() {
        // GIVEN
        String email = "example@email.com"; // Existing user's email
        Users existingUser = new Users("Mike", "Myers", email, "password123"); // Existing user details
        Users updatedUser = new Users("Michael", "Meyers", "new@example.com", "newPassword123"); // Updated user details
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser)); // Mock finding existing user by email
        given(userRepository.existsByEmail("new@example.com")).willReturn(false); // Mock check for new email not being taken

        // WHEN
        userService.updateUser(email, updatedUser); // Call the updateUser method with the existing email and updated user details

        // THEN
        verify(userRepository).findByEmail(email); // Verify that the findByEmail method is called with the existing email
        verify(userRepository).existsByEmail("new@example.com"); // Verify that the existsByEmail method is called with the new email
        verify(userRepository).save(existingUser); // Verify that the save method is called with the updated user details
    }

    // Focus on level of details verifying that the fields of the existing user are updated correctly and saved
    @Test
    void testUpdateUser_ValidUpdate() {
        // GIVEN
        // Create an existing user with predefined email and password
        Users existingUser = new Users();
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("OldPass123");

        // Create a user with updated details (new email, new password, new first name, and last name)
        Users updatedUser = new Users();
        updatedUser.setEmail("new@example.com");
        updatedUser.setPassword("NewPass123");
        updatedUser.setFirstName("Katherine");
        updatedUser.setLastName("Ryan");

        // Mock the behavior of the user repository:
        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser)); // When the repository finds a user by the existing email, return the existing user
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(false); // When checking if the new email already exists, return false (meaning the email does not exist)

        // WHEN
        userService.updateUser(existingUser.getEmail(), updatedUser); // Call the updateUser method of the UserService with the existing user's email and the new user details

        // THEN
        assertEquals(updatedUser.getEmail(), existingUser.getEmail()); // Assert that the existing user's email has been updated to the new email
        assertEquals(updatedUser.getPassword(), existingUser.getPassword()); // Assert that the existing user's password has been updated to the new password
        assertEquals(updatedUser.getFirstName(), existingUser.getFirstName()); // Assert that the existing user's first name has been updated to the new first name
        assertEquals(updatedUser.getLastName(), existingUser.getLastName()); // Assert that the existing user's last name has been updated to the new last name
        verify(userRepository, times(1)).save(existingUser); // Verify that the repository's save method was called exactly once to save the updated user
    }

    @Test
    void updateUser_UserNotFound() {
        // GIVEN
        String email = "example@email.com";
        Users updatedUser = new Users("Mike", "Myers", email, "newPassword");
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User with email " + email + " does not exist");

        verify(userRepository).findByEmail(email); // Verify userRepository.findByEmail() was called
        verify(userRepository, never()).save(any(Users.class)); // Verify userRepository.save() was never called
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
        // Assert that updating the user with an existing email throws an IllegalArgumentException with a relevant message
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalArgumentException.class) // Expect IllegalArgumentException
                .hasMessageContaining("Email already exists"); // Check the exception message

        verify(userRepository).findByEmail(email); // Verify userRepository.findByEmail() was called
        verify(userRepository).existsByEmail(newEmail); // Verify userRepository.existsByEmail() was called
        verify(userRepository, never()).save(any(Users.class)); // Verify userRepository.save() was never called
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

        verify(userRepository).findByEmail(email); // Verify userRepository.findByEmail() was called
        verify(userRepository, never()).save(any(Users.class)); // Verify userRepository.save() was never called
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

        verify(userRepository).findByEmail(email); // Verify userRepository.findByEmail() was called
        verify(userRepository, never()).save(any(Users.class)); // Verify userRepository.save() was never called
    }

    @Test
    void updateUser_FirstNameTooLong() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("Mike", "Myers", email, "password123");
        Users updatedUser = new Users("M".repeat(51), "Myers", email, "password123");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("First name must be less than 50 characters long");

        verify(userRepository).findByEmail(email); // Verify userRepository.findByEmail() was called
        verify(userRepository, never()).save(any(Users.class)); // Verify userRepository.save() was never called
    }

    @Test
    void updateUser_LastNameTooLong() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("Mike", "Myers", email, "password123");
        Users updatedUser = new Users("Mike", "M".repeat(101), email, "password123");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, updatedUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Last name must be less than 100 characters long");

        verify(userRepository).findByEmail(email); // Verify userRepository.findByEmail() was called
        verify(userRepository, never()).save(any(Users.class)); // Verify userRepository.save() was never called
    }

    @Test
    void updateUser_EmptyFirstName() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("", "Myers", "", "");

        Users updatedUser = new Users("Mike", "Myers", "", ""); // Update user object with valid first name
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser)); // Mock the repository to return the existing user when the email is queried

        // WHEN
        userService.updateUser(email, updatedUser); // Call the updateUser method to update the user details

        // THEN
        verify(userRepository).save(existingUser); // Verify that the save method was called once with the updated user details
    }

    @Test
    void updateUser_EmptyLastName() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("Mike", "", "", "");
        Users updatedUser = new Users("Mike", "Myers", "", ""); // Update user object with valid last name

        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser)); // Mock the repository to return the existing user when the email is queried

        // WHEN
        userService.updateUser(email, updatedUser); // The service is called to update the user's details

        // THEN
        verify(userRepository).save(existingUser); // Verify that the save method was called once with the updated user details
    }

    @Test
    void updateUser_EmptyFirstNameAndLastName() {
        // GIVEN
        String email = "example@email.com";
        Users existingUser = new Users("", "", "", "");

        Users updatedUser = new Users("Mike", "Myers", "", ""); // Update user object with valid first name and last name
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser)); // Mock the repository to return the existing user when the email is queried

        // WHEN
        userService.updateUser(email, updatedUser); // Call the updateUser method to update the user details

        // THEN
        verify(userRepository).save(existingUser); // Verify that the save method was called once with the updated user details
    }

    @Test
    void updateUser_NoFieldsUpdated() {
        // GIVEN
        String email = ""; // Both the existing and updated users have all fields blank
        Users existingUser = new Users("", "", "", ""); // Existing user with blank fields
        Users updatedUser = new Users("", "", "", ""); // Updated user with blank fields (no changes)

        // Mock repository behavior
        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        // WHEN & THEN
        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(email, updatedUser);
        });

        assertEquals("No fields updated", thrownException.getMessage()); // Verify the exception message
        verify(userRepository).findByEmail(email); // Verify userRepository.findByEmail() was called
        verify(userRepository, never()).save(any(Users.class)); // Verify userRepository.save() was never called
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
                .hasMessageContaining("User with email " + email + " does not exist");

        verify(userRepository).findByEmail(email); // Verify that userRepository.findByEmail() was called with the correct email
        verify(userRepository, never()).delete(any(Users.class)); // Verify that userRepository.delete() was never called
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
        verify(userRepository).findByEmail(email); // Verify that userRepository.findByEmail() was called with the correct email
        verify(userRepository).delete(user); // Verify that userRepository.delete() was called with the correct user
    }
}