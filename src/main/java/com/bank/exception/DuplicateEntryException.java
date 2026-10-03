package com.bank.exception;

/** Thrown when trying to insert a customer/account that already exists (e.g. duplicate email). */
public class DuplicateEntryException extends BankException {
    public DuplicateEntryException(String message) {
        super(message);
    }
}
