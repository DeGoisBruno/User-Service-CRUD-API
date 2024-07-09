# UserServiceTest Scenarios

## getUser

### Scenario Description
Testing the retrieval of all users.

### Given
UserRepository returns all users.

### When
`getUser()` method is called.

### Then
Ensure that `userRepository.findAll()` is invoked.

---

## getUserById

### Scenario Description
Testing the retrieval of a specific user by ID.

### Given
UserRepository returns a user with ID 1.

### When
`getUserById()` method is called with user ID 1.

### Then
Ensure that the returned user matches the expected user.

---

## willThrowWhenUserNotFound

### Scenario Description
Testing the case where a user is not found by ID.

### Given
UserRepository returns an empty optional for user ID 1.

### When
`getUserById()` method is called with user ID 1.

### Then
Expect an `IllegalStateException` indicating user with ID 1 does not exist.

---

## addNewUser

### Scenario Description
Testing the addition of a new user.

### Given
A new user object is saved to UserRepository.

### When
`addNewUser()` method is called with the new user.

### Then
Ensure that `userRepository.save()` is called with the new user object.

---

## willThrowWhenEmailIsTaken

### Scenario Description
Testing the case where a user with a taken email cannot be added.

### Given
UserRepository indicates that the email of the new user is already taken.

### When
`addNewUser()` method is called with a user whose email is already in use.

### Then
Expect a `BadRequestException` indicating that the email is already taken.

---

## updateUser_Success

### Scenario Description
Testing the update of an existing user's details.

### Given
UserRepository returns an existing user with ID 1.

### When
`updateUser()` method is called with updated details.

### Then
Ensure that `userRepository.save()` is called with the updated user object and verify the updated user details.

---

## updateUserNotFound

### Scenario Description
Testing the case where updateUser fails because the user with the given ID does not exist.

### Given
UserRepository returns an empty optional for user ID 1.

### When
`updateUser()` method is called with user ID 1 and updated details.

### Then
Expect an `IllegalStateException` indicating user with ID 1 does not exist, and ensure `userRepository.save()` is not called.

---

## updateUser_WithDifferentFirstName

### Scenario Description
Testing the update of an existing user's details where only the first name is changed.

### Given
UserRepository returns an existing user with ID 1.

### When
`updateUser()` method is called with updated first name and unchanged last name and email.

### Then
Ensure that `userRepository.save()` is called with the updated user object and verify the updated first name.

---

## updateUser_WithSameFirstName

### Scenario Description
Testing the update of an existing user's details where all details remain the same.

### Given
UserRepository returns an existing user with ID 1.

### When
`updateUser()` method is called with details identical to the existing user.

### Then
Ensure that `userRepository.save()` is not called and verify that user details remain unchanged.

---

## updateUser_WithNullLastName

### Scenario Description
Testing the update of an existing user's details where the last name is set to null.

### Given
UserRepository returns an existing user with ID 1.

### When
`updateUser()` method is called with updated first name and null last name.

### Then
Ensure that `userRepository.save()` is called with the updated user object and verify the last name remains unchanged.

---

## updateUser_WithDifferentEmail

### Scenario Description
Testing the update of an existing user's email address.

### Given
UserRepository returns an existing user with ID 1.

### When
`updateUser()` method is called with updated email address.

### Then
Ensure that `userRepository.save()` is called with the updated user object and verify the email address is updated.

---

## deleteUser

### Scenario Description
Testing the deletion of an existing user.

### Given
UserRepository indicates that a user with ID 1 exists.

### When
`deleteUser()` method is called with user ID 1.

### Then
Ensure that `userRepository.deleteByEmail()` is called.

---

## deleteUserNotFound

### Scenario Description
Testing the case where deleteUser fails because the user with the given ID does not exist.

### Given
UserRepository indicates that no user with ID 1 exists.

### When
`deleteUser()` method is called with user ID 1.

### Then
Expect an `IllegalStateException` indicating user with ID 1 does not exist, and ensure `userRepository.deleteByEmail()` is not called.

