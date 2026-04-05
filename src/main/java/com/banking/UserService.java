package com.banking;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Manages user registration, authentication, and profile data.
 */
public class UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final Map<String, String> users; // email -> hashedPassword (simulated)
    private final Map<String, String> profiles; // email -> full name

    public UserService() {
        this.users = new HashMap<>();
        this.profiles = new HashMap<>();
    }

    public void register(String email, String password, String fullName) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be blank.");
        }
        if (users.containsKey(email.toLowerCase())) {
            throw new IllegalStateException("User already exists with email: " + email);
        }
        users.put(email.toLowerCase(), simpleHash(password));
        profiles.put(email.toLowerCase(), fullName.trim());
    }

    public boolean login(String email, String password) {
        if (email == null || password == null) return false;
        String key = email.toLowerCase();
        return users.containsKey(key) && users.get(key).equals(simpleHash(password));
    }

    public void updatePassword(String email, String oldPassword, String newPassword) {
        if (!login(email, oldPassword)) {
            throw new IllegalStateException("Authentication failed.");
        }
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("New password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        users.put(email.toLowerCase(), simpleHash(newPassword));
    }

    public boolean userExists(String email) {
        if (email == null) return false;
        return users.containsKey(email.toLowerCase());
    }

    public String getFullName(String email) {
        if (email == null) return null;
        return profiles.get(email.toLowerCase());
    }

    public int getUserCount() {
        return users.size();
    }

    /** Simulated hash — not for production use. */
    private String simpleHash(String input) {
        return "hash:" + Integer.toHexString(input.hashCode());
    }
}
