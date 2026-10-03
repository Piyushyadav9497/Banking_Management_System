package com.bank.exception;

/**
 * Base checked exception for all banking-domain errors.
 * All other custom exceptions extend this, so callers can catch
 * BankException broadly, or the specific subtype when they need to.
 */
public class BankException extends Exception {
    public BankException(String message) {
        super(message);
    }

    public BankException(String message, Throwable cause) {
        super(message, cause);
    }
}
