package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void addTransaction(Transaction txn) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, type, amount, balance_after, related_account, txn_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, txn.getAccountId());
            ps.setString(2, txn.getType().name());
            ps.setBigDecimal(3, txn.getAmount());
            ps.setBigDecimal(4, txn.getBalanceAfter());
            ps.setString(5, txn.getRelatedAccount());
            ps.setTimestamp(6, Timestamp.valueOf(txn.getTxnTime()));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    txn.setId(keys.getInt(1));
                }
            }
        }
    }

    public List<Transaction> getTransactionsForAccount(int accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY txn_time DESC";
        List<Transaction> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY id";
        List<Transaction> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getInt("account_id"),
                TransactionType.valueOf(rs.getString("type")),
                rs.getBigDecimal("amount"),
                rs.getBigDecimal("balance_after"),
                rs.getString("related_account"),
                rs.getTimestamp("txn_time").toLocalDateTime()
        );
    }
}
