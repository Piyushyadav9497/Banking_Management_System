package com.bank.exception;

/** Thrown when a requested customer does not exist in the system. */
public class CustomerNotFoundException extends BankException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
