package com.bank.exception;

/** Thrown when an operation is attempted on an account that has been closed. */
public class AccountClosedException extends BankException {
    public AccountClosedException(String message) {
        super(message);
    }
}
