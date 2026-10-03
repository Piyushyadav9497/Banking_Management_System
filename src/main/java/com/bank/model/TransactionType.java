package com.bank.model;

/** Kinds of ledger entries recorded against an account. */
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_OUT,
    TRANSFER_IN
}
