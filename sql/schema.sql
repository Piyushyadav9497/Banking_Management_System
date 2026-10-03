-- Banking Management System - Database Schema
-- Run this once in MySQL before starting the application.

CREATE DATABASE IF NOT EXISTS bank_db;
USE bank_db;

CREATE TABLE IF NOT EXISTS customers (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    email   VARCHAR(100) NOT NULL UNIQUE,
    phone   VARCHAR(15)  NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    account_number  VARCHAR(20) NOT NULL UNIQUE,
    customer_id     INT NOT NULL,
    account_type    VARCHAR(20) NOT NULL,   -- SAVINGS or CURRENT
    balance         DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(10) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE or CLOSED
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    account_id      INT NOT NULL,
    type            VARCHAR(20) NOT NULL,   -- DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN
    amount          DECIMAL(15,2) NOT NULL,
    balance_after   DECIMAL(15,2) NOT NULL,
    related_account VARCHAR(20) NULL,       -- for transfers, the other account's number
    txn_time        DATETIME NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Sample data (optional)
INSERT INTO customers (name, email, phone) VALUES
('Asha Verma', 'asha.verma@example.com', '9876543210');

INSERT INTO accounts (account_number, customer_id, account_type, balance) VALUES
('AC1000001', 1, 'SAVINGS', 5000.00);
