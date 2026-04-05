package com.banking;

/**
 * Validates password strength based on configurable rules.
 */
public class PasswordValidator {

    public enum Strength { WEAK, MODERATE, STRONG, VERY_STRONG }

    private final int minLength;
    private final boolean requireUppercase;
    private final boolean requireLowercase;
    private final boolean requireDigit;
    private final boolean requireSpecial;

    public PasswordValidator() {
        this(8, true, true, true, true);
    }

    public PasswordValidator(int minLength, boolean requireUppercase,
                              boolean requireLowercase, boolean requireDigit,
                              boolean requireSpecial) {
        if (minLength < 1) throw new IllegalArgumentException("Minimum length must be at least 1.");
        this.minLength = minLength;
        this.requireUppercase = requireUppercase;
        this.requireLowercase = requireLowercase;
        this.requireDigit = requireDigit;
        this.requireSpecial = requireSpecial;
    }

    /**
     * Returns true if all configured rules pass.
     */
    public boolean isValid(String password) {
        if (password == null) return false;
        if (password.length() < minLength) return false;
        if (requireUppercase && !password.chars().anyMatch(Character::isUpperCase)) return false;
        if (requireLowercase && !password.chars().anyMatch(Character::isLowerCase)) return false;
        if (requireDigit && !password.chars().anyMatch(Character::isDigit)) return false;
        if (requireSpecial && !password.chars().anyMatch(c -> "!@#$%^&*()-_=+[]{}|;:',.<>?/`~".indexOf(c) >= 0)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the strength of the given password.
     */
    public Strength getStrength(String password) {
        if (password == null || password.length() < minLength) return Strength.WEAK;

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.chars().anyMatch(Character::isUpperCase)) score++;
        if (password.chars().anyMatch(Character::isLowerCase)) score++;
        if (password.chars().anyMatch(Character::isDigit)) score++;
        if (password.chars().anyMatch(c -> "!@#$%^&*()-_=+[]{}|;:',.<>?/`~".indexOf(c) >= 0)) score++;

        if (score <= 2) return Strength.WEAK;
        if (score == 3) return Strength.MODERATE;
        if (score <= 5) return Strength.STRONG;
        return Strength.VERY_STRONG;
    }

    /**
     * Returns a descriptive validation message.
     */
    public String getValidationMessage(String password) {
        if (password == null) return "Password cannot be null.";
        if (password.length() < minLength) return "Password too short (min " + minLength + " characters).";
        if (requireUppercase && !password.chars().anyMatch(Character::isUpperCase))
            return "Password must contain at least one uppercase letter.";
        if (requireLowercase && !password.chars().anyMatch(Character::isLowerCase))
            return "Password must contain at least one lowercase letter.";
        if (requireDigit && !password.chars().anyMatch(Character::isDigit))
            return "Password must contain at least one digit.";
        if (requireSpecial && !password.chars().anyMatch(c -> "!@#$%^&*()-_=+[]{}|;:',.<>?/`~".indexOf(c) >= 0))
            return "Password must contain at least one special character.";
        return "Password is valid.";
    }

    public int getMinLength() { return minLength; }
}
