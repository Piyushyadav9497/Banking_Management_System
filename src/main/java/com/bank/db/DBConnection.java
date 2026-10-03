package com.bank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages a single shared JDBC connection to the MySQL database.
 * Update the URL / USER / PASSWORD via environment variables, or edit
 * the defaults below directly for local development.
 */
public final class DBConnection {

    private static final String URL =
            System.getenv().getOrDefault("BANK_DB_URL", "jdbc:mysql://localhost:3306/bank_db");
    private static final String USER =
            System.getenv().getOrDefault("BANK_DB_USER", "root");
    private static final String PASSWORD =
            System.getenv().getOrDefault("BANK_DB_PASSWORD", "password");

    private static Connection connection;

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found on classpath.", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing DB connection: " + e.getMessage());
            }
        }
    }
}
