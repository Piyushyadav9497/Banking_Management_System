package com.bank.exception;

/** Thrown when a requested account does not exist in the system. */
public class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
