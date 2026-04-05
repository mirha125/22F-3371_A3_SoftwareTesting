package com.banking;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@DisplayName("UserService Tests")
@TestMethodOrder(OrderAnnotation.class)
class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    // ─── Positive Tests ───────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("should_RegisterUser_when_ValidEmailAndPassword")
    void should_RegisterUser_when_ValidEmailAndPassword() {
        // Arrange
        String email = "saad@example.com";
        String password = "SecurePass1!";
        String name = "Saad Zafar";

        // Act
        userService.register(email, password, name);

        // Assert
        assertTrue(userService.userExists(email));
    }

    @Test
    @Order(2)
    @DisplayName("should_LoginSuccessfully_when_CorrectCredentials")
    void should_LoginSuccessfully_when_CorrectCredentials() {
        // Arrange
        userService.register("user@test.com", "Password1!", "Test User");

        // Act
        boolean result = userService.login("user@test.com", "Password1!");

        // Assert
        assertTrue(result);
    }

    @Test
    @Order(3)
    @DisplayName("should_ReturnFullName_when_UserIsRegistered")
    void should_ReturnFullName_when_UserIsRegistered() {
        // Arrange
        userService.register("ali@bank.com", "Ali12345!", "Ali Hassan");

        // Act
        String name = userService.getFullName("ali@bank.com");

        // Assert
        assertEquals("Ali Hassan", name);
    }

    @Test
    @Order(4)
    @DisplayName("should_UpdatePassword_when_OldPasswordIsCorrect")
    void should_UpdatePassword_when_OldPasswordIsCorrect() {
        // Arrange
        userService.register("pass@test.com", "OldPass1!", "Test");

        // Act
        userService.updatePassword("pass@test.com", "OldPass1!", "NewPass2@");

        // Assert
        assertTrue(userService.login("pass@test.com", "NewPass2@"));
        assertFalse(userService.login("pass@test.com", "OldPass1!"));
    }

    @Test
    @Order(5)
    @DisplayName("should_BeCaseInsensitive_when_CheckingEmail")
    void should_BeCaseInsensitive_when_CheckingEmail() {
        // Arrange
        userService.register("Admin@Bank.com", "Admin123!", "Admin");

        // Act / Assert
        assertTrue(userService.userExists("admin@bank.com"));
        assertTrue(userService.login("ADMIN@BANK.COM", "Admin123!"));
    }

    // ─── Negative Tests ───────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("should_ThrowException_when_EmailIsInvalidFormat")
    void should_ThrowException_when_EmailIsInvalidFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.register("not-an-email", "Pass1234!", "Name"));
    }

    @Test
    @Order(7)
    @DisplayName("should_ThrowException_when_PasswordIsTooShort")
    void should_ThrowException_when_PasswordIsTooShort() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.register("user@test.com", "abc", "Name"));
    }

    @Test
    @Order(8)
    @DisplayName("should_ThrowException_when_RegisteringDuplicateEmail")
    void should_ThrowException_when_RegisteringDuplicateEmail() {
        // Arrange
        userService.register("dup@test.com", "ValidPass1!", "First");

        // Act / Assert
        assertThrows(IllegalStateException.class,
                () -> userService.register("dup@test.com", "AnotherPass1!", "Second"));
    }

    @Test
    @Order(9)
    @DisplayName("should_ReturnFalse_when_LoginWithWrongPassword")
    void should_ReturnFalse_when_LoginWithWrongPassword() {
        // Arrange
        userService.register("user2@test.com", "Correct1!", "User");

        // Act
        boolean result = userService.login("user2@test.com", "WrongPass");

        // Assert
        assertFalse(result);
    }

    // ─── Boundary / Exception Tests ──────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("should_ReturnFalse_when_LoginWithNullCredentials")
    void should_ReturnFalse_when_LoginWithNullCredentials() {
        assertFalse(userService.login(null, null));
    }

    @Test
    @Order(11)
    @DisplayName("should_ThrowException_when_PasswordUpdateWithWrongOldPassword")
    void should_ThrowException_when_PasswordUpdateWithWrongOldPassword() {
        // Arrange
        userService.register("upd@test.com", "Correct1!", "User");

        // Act / Assert
        assertThrows(IllegalStateException.class,
                () -> userService.updatePassword("upd@test.com", "WrongOld!", "NewPass1!"));
    }

    @Test
    @Order(12)
    @DisplayName("should_ReturnNull_when_GettingNameOfNonexistentUser")
    void should_ReturnNull_when_GettingNameOfNonexistentUser() {
        assertNull(userService.getFullName("nobody@nowhere.com"));
    }
}
