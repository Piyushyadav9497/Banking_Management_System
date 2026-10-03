package com.bank.model;

/**
 * The two account types supported by the system.
 * Using an enum (rather than a free-text String) prevents invalid
 * account types from ever being stored.
 */
public enum AccountType {
    SAVINGS,
    CURRENT;

    /** Parses user input case-insensitively, returns null if invalid. */
    public static AccountType fromString(String value) {
        if (value == null) return null;
        try {
            return AccountType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
