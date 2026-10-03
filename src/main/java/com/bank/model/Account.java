package com.bank.model;

import java.math.BigDecimal;

/**
 * Represents a bank Account belonging to a Customer.
 * Uses BigDecimal for money to avoid floating-point rounding errors.
 */
public class Account {
    private int id;
    private String accountNumber;
    private int customerId;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;

    public Account() {
    }

    public Account(String accountNumber, int customerId, AccountType accountType, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.status = AccountStatus.ACTIVE;
    }

    public Account(int id, String accountNumber, int customerId, AccountType accountType,
                    BigDecimal balance, AccountStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return String.format("[%d] Acc#%-12s | CustomerID:%-4d | %-8s | Balance: %10.2f | %s",
                id, accountNumber, customerId, accountType, balance, status);
    }
}
