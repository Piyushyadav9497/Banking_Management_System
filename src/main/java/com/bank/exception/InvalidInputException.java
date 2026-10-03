package com.bank.exception;

/** Thrown when user-supplied input fails validation (bad email, negative amount, etc.). */
public class InvalidInputException extends BankException {
    public InvalidInputException(String message) {
        super(message);
    }
}
