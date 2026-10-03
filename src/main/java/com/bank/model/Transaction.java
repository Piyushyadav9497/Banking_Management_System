package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents one ledger entry: a deposit, withdrawal, or one leg of a transfer.
 * A transfer between two accounts is recorded as two rows (TRANSFER_OUT on the
 * sender's account, TRANSFER_IN on the receiver's) so each account's history
 * is self-contained.
 */
public class Transaction {
    private int id;
    private int accountId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String relatedAccount; // account number of the other side, for transfers
    private LocalDateTime txnTime;

    public Transaction() {
    }

    public Transaction(int accountId, TransactionType type, BigDecimal amount,
                        BigDecimal balanceAfter, String relatedAccount, LocalDateTime txnTime) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.relatedAccount = relatedAccount;
        this.txnTime = txnTime;
    }

    public Transaction(int id, int accountId, TransactionType type, BigDecimal amount,
                        BigDecimal balanceAfter, String relatedAccount, LocalDateTime txnTime) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.relatedAccount = relatedAccount;
        this.txnTime = txnTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getRelatedAccount() {
        return relatedAccount;
    }

    public void setRelatedAccount(String relatedAccount) {
        this.relatedAccount = relatedAccount;
    }

    public LocalDateTime getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(LocalDateTime txnTime) {
        this.txnTime = txnTime;
    }

    @Override
    public String toString() {
        String related = relatedAccount == null ? "" : " (with " + relatedAccount + ")";
        return String.format("Txn[%d] AccountID:%d | %-13s | Amount: %10.2f | Balance after: %10.2f | %s%s",
                id, accountId, type, amount, balanceAfter, txnTime, related);
    }
}
