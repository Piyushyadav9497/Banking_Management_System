package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DuplicateEntryException;
import com.bank.model.Account;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public void addAccount(Account account) throws DuplicateEntryException, SQLException {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, account.getAccountNumber());
            ps.setInt(2, account.getCustomerId());
            ps.setString(3, account.getAccountType().name());
            ps.setBigDecimal(4, account.getBalance());
            ps.setString(5, account.getStatus().name());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    account.setId(keys.getInt(1));
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("Account number " + account.getAccountNumber() + " already exists.");
        }
    }

    public Account getAccountById(int id) throws AccountNotFoundException, SQLException {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        throw new AccountNotFoundException("No account found with ID " + id);
    }

    public Account getAccountByNumber(String accountNumber) throws AccountNotFoundException, SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        throw new AccountNotFoundException("No account found with number " + accountNumber);
    }

    public List<Account> getAccountsByCustomer(int customerId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY id";
        List<Account> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    public List<Account> getAllAccounts() throws SQLException {
        String sql = "SELECT * FROM accounts ORDER BY id";
        List<Account> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    public void updateBalance(int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    public void updateStatus(int accountId, AccountStatus status) throws SQLException {
        String sql = "UPDATE accounts SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
                rs.getInt("id"),
                rs.getString("account_number"),
                rs.getInt("customer_id"),
                AccountType.valueOf(rs.getString("account_type")),
                rs.getBigDecimal("balance"),
                AccountStatus.valueOf(rs.getString("status"))
        );
    }
}
