package com.bank.util;

import com.bank.exception.InvalidInputException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Centralized validation logic. All static methods throw InvalidInputException
 * with a clear message when a rule is violated, instead of returning booleans,
 * so calling code is forced to handle bad input explicitly.
 */
public final class Validator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{10}$");

    private Validator() {
        // utility class, no instances
    }

    public static void requireNonEmpty(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }

    public static void validateEmail(String email) throws InvalidInputException {
        requireNonEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidInputException("Invalid email format: " + email);
        }
    }

    public static void validatePhone(String phone) throws InvalidInputException {
        requireNonEmpty(phone, "Phone number");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidInputException("Phone number must be exactly 10 digits: " + phone);
        }
    }

    public static int parsePositiveInt(String value, String fieldName) throws InvalidInputException {
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed <= 0) {
                throw new InvalidInputException(fieldName + " must be a positive number.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid whole number.");
        }
    }

    /** Parses and validates a monetary amount: must be a valid number, > 0, max 2 decimal places. */
    public static BigDecimal parsePositiveAmount(String value, String fieldName) throws InvalidInputException {
        requireNonEmpty(value, fieldName);
        BigDecimal amount;
        try {
            amount = new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid number (e.g. 500.00).");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException(fieldName + " must be greater than zero.");
        }
        if (amount.scale() > 2) {
            throw new InvalidInputException(fieldName + " cannot have more than 2 decimal places.");
        }
        return amount;
    }

    /** Parses a non-negative initial balance (zero is allowed when opening an account). */
    public static BigDecimal parseNonNegativeAmount(String value, String fieldName) throws InvalidInputException {
        requireNonEmpty(value, fieldName);
        BigDecimal amount;
        try {
            amount = new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid number (e.g. 1000.00).");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException(fieldName + " cannot be negative.");
        }
        if (amount.scale() > 2) {
            throw new InvalidInputException(fieldName + " cannot have more than 2 decimal places.");
        }
        return amount;
    }
}
