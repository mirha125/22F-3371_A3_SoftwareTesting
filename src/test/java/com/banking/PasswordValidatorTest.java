package com.banking;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@DisplayName("PasswordValidator Tests")
@TestMethodOrder(OrderAnnotation.class)
class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }

    // ─── Positive Tests ───────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("should_ReturnTrue_when_PasswordMeetsAllRules")
    void should_ReturnTrue_when_PasswordMeetsAllRules() {
        // Arrange / Act / Assert
        assertTrue(validator.isValid("Str0ng@Pass"));
    }

    @Test
    @Order(2)
    @DisplayName("should_ReturnVeryStrong_when_LongPasswordWithAllTypes")
    void should_ReturnVeryStrong_when_LongPasswordWithAllTypes() {
        // Arrange / Act
        PasswordValidator.Strength strength = validator.getStrength("MyStr0ng!Pass#2026");

        // Assert
        assertEquals(PasswordValidator.Strength.VERY_STRONG, strength);
    }

    @Test
    @Order(3)
    @DisplayName("should_ReturnValidMessage_when_PasswordIsValid")
    void should_ReturnValidMessage_when_PasswordIsValid() {
        // Arrange / Act
        String msg = validator.getValidationMessage("Abc123!x");

        // Assert
        assertEquals("Password is valid.", msg);
    }

    @Test
    @Order(4)
    @DisplayName("should_AllowCustomMinLength_when_ConfiguredWithConstructor")
    void should_AllowCustomMinLength_when_ConfiguredWithConstructor() {
        // Arrange
        PasswordValidator custom = new PasswordValidator(4, false, false, false, false);

        // Act / Assert
        assertTrue(custom.isValid("abcd"));
        assertEquals(4, custom.getMinLength());
    }

    @Test
    @Order(5)
    @DisplayName("should_ReturnStrong_when_PasswordMeetsMostCriteria")
    void should_ReturnStrong_when_PasswordMeetsMostCriteria() {
        // Arrange / Act
        PasswordValidator.Strength strength = validator.getStrength("Abcdefg1");

        // Assert — 8 chars + upper + lower + digit = score 4 = STRONG
        assertEquals(PasswordValidator.Strength.STRONG, strength);
    }

    // ─── Negative Tests ───────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("should_ReturnFalse_when_PasswordIsNull")
    void should_ReturnFalse_when_PasswordIsNull() {
        assertFalse(validator.isValid(null));
    }

    @Test
    @Order(7)
    @DisplayName("should_ReturnFalse_when_PasswordTooShort")
    void should_ReturnFalse_when_PasswordTooShort() {
        assertFalse(validator.isValid("Ab1!"));
    }

    @Test
    @Order(8)
    @DisplayName("should_ReturnFalse_when_MissingUppercase")
    void should_ReturnFalse_when_MissingUppercase() {
        assertFalse(validator.isValid("abcdef1!"));
    }

    @Test
    @Order(9)
    @DisplayName("should_ReturnFalse_when_MissingDigit")
    void should_ReturnFalse_when_MissingDigit() {
        assertFalse(validator.isValid("Abcdefg!"));
    }

    @Test
    @Order(10)
    @DisplayName("should_ReturnFalse_when_MissingSpecialChar")
    void should_ReturnFalse_when_MissingSpecialChar() {
        assertFalse(validator.isValid("Abcdefg1"));
    }

    // ─── Boundary / Exception Tests ──────────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("should_ReturnWeak_when_NullPassword")
    void should_ReturnWeak_when_NullPassword() {
        assertEquals(PasswordValidator.Strength.WEAK, validator.getStrength(null));
    }

    @Test
    @Order(12)
    @DisplayName("should_ReturnCorrectMessage_when_PasswordMissingLowercase")
    void should_ReturnCorrectMessage_when_PasswordMissingLowercase() {
        // Arrange — all uppercase, digit, special, long enough
        String msg = validator.getValidationMessage("ABCDEFG1!");

        // Assert
        assertEquals("Password must contain at least one lowercase letter.", msg);
    }

    @Test
    @Order(13)
    @DisplayName("should_ReturnCorrectMessage_when_NullPassword")
    void should_ReturnCorrectMessage_when_NullPassword() {
        assertEquals("Password cannot be null.", validator.getValidationMessage(null));
    }

    @Test
    @Order(14)
    @DisplayName("should_ReturnCorrectMessage_when_TooShort")
    void should_ReturnCorrectMessage_when_TooShort() {
        String msg = validator.getValidationMessage("Ab1!");
        assertTrue(msg.contains("too short"));
    }

    @Test
    @Order(15)
    @DisplayName("should_ThrowException_when_MinLengthIsZero")
    void should_ThrowException_when_MinLengthIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new PasswordValidator(0, true, true, true, true));
    }

    @Test
    @Order(16)
    @DisplayName("should_ReturnModerate_when_PasswordHasThreeCriteria")
    void should_ReturnModerate_when_PasswordHasThreeCriteria() {
        // short password (>= 8 chars) + lowercase only → score = 1(len>=8) + 1(lower) = 2... need 3
        // Let's use: 8 chars + upper + lower = score 3 (len>=8, upper, lower)
        PasswordValidator lenient = new PasswordValidator(4, false, false, false, false);
        PasswordValidator.Strength strength = lenient.getStrength("Abcdefgh");
        assertEquals(PasswordValidator.Strength.MODERATE, strength);
    }

    @Test
    @Order(17)
    @DisplayName("should_ReturnCorrectMessage_when_MissingDigit")
    void should_ReturnCorrectMessage_when_MissingDigit() {
        String msg = validator.getValidationMessage("Abcdefg!x");
        assertEquals("Password must contain at least one digit.", msg);
    }

    @Test
    @Order(18)
    @DisplayName("should_ReturnCorrectMessage_when_MissingSpecialChar")
    void should_ReturnCorrectMessage_when_MissingSpecialChar() {
        String msg = validator.getValidationMessage("Abcdefg1x");
        assertEquals("Password must contain at least one special character.", msg);
    }
}
