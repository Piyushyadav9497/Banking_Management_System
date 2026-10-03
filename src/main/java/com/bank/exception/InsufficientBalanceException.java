package com.bank.exception;

/** Thrown when a withdrawal or transfer would take an account below zero. */
public class InsufficientBalanceException extends BankException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
