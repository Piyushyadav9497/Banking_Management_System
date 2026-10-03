package com.bank.service;

import com.bank.dao.AccountDAO;
import com.bank.dao.CustomerDAO;
import com.bank.dao.TransactionDAO;
import com.bank.exception.*;
import com.bank.model.*;
import com.bank.util.Validator;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Business logic layer. Coordinates between DAOs, applies validation,
 * and enforces banking rules (can't withdraw more than the balance,
 * can't operate on a closed account, etc.).
 * This is the layer the UI (Main.java) talks to — it never touches SQL directly.
 */
public class BankingService {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    // ---------- Customer operations ----------

    public Customer addCustomer(String name, String email, String phone)
            throws InvalidInputException, DuplicateEntryException, SQLException {
        Validator.requireNonEmpty(name, "Name");
        Validator.validateEmail(email);
        Validator.validatePhone(phone);

        Customer customer = new Customer(name, email, phone);
        customerDAO.addCustomer(customer);
        return customer;
    }

    public List<Customer> listAllCustomers() throws SQLException {
        return customerDAO.getAllCustomers();
    }

    public void deleteCustomer(String idStr) throws InvalidInputException, CustomerNotFoundException, SQLException {
        int id = Validator.parsePositiveInt(idStr, "Customer ID");
        customerDAO.deleteCustomer(id);
    }

    // ---------- Account operations ----------

    public Account openAccount(String customerIdStr, String accountTypeStr, String initialBalanceStr)
            throws InvalidInputException, CustomerNotFoundException, DuplicateEntryException, SQLException {

        int customerId = Validator.parsePositiveInt(customerIdStr, "Customer ID");
        customerDAO.getCustomerById(customerId); // throws CustomerNotFoundException if missing

        AccountType type = AccountType.fromString(accountTypeStr);
        if (type == null) {
            throw new InvalidInputException("Account type must be SAVINGS or CURRENT.");
        }

        BigDecimal initialBalance = Validator.parseNonNegativeAmount(initialBalanceStr, "Initial balance");

        String accountNumber = generateAccountNumber();
        Account account = new Account(accountNumber, customerId, type, initialBalance);
        accountDAO.addAccount(account);
        return account;
    }

    public Account getAccountByNumber(String accountNumber) throws AccountNotFoundException, SQLException {
        return accountDAO.getAccountByNumber(accountNumber);
    }

    public List<Account> listAllAccounts() throws SQLException {
        return accountDAO.getAllAccounts();
    }

    public List<Account> listAccountsForCustomer(String customerIdStr)
            throws InvalidInputException, SQLException {
        int customerId = Validator.parsePositiveInt(customerIdStr, "Customer ID");
        return accountDAO.getAccountsByCustomer(customerId);
    }

    public void closeAccount(String accountNumber) throws AccountNotFoundException, BankException, SQLException {
        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BankException("Cannot close account " + accountNumber +
                    " — balance must be zero first (current: " + account.getBalance() + ").");
        }
        accountDAO.updateStatus(account.getId(), AccountStatus.CLOSED);
    }

    // ---------- Transaction operations ----------

    public Transaction deposit(String accountNumber, String amountStr)
            throws InvalidInputException, AccountNotFoundException, AccountClosedException, SQLException {

        Validator.requireNonEmpty(accountNumber, "Account number");
        BigDecimal amount = Validator.parsePositiveAmount(amountStr, "Deposit amount");

        Account account = accountDAO.getAccountByNumber(accountNumber);
        ensureActive(account);

        BigDecimal newBalance = account.getBalance().add(amount);
        accountDAO.updateBalance(account.getId(), newBalance);

        Transaction txn = new Transaction(account.getId(), TransactionType.DEPOSIT, amount,
                newBalance, null, LocalDateTime.now());
        transactionDAO.addTransaction(txn);
        return txn;
    }

    public Transaction withdraw(String accountNumber, String amountStr)
            throws InvalidInputException, AccountNotFoundException, AccountClosedException,
            InsufficientBalanceException, SQLException {

        Validator.requireNonEmpty(accountNumber, "Account number");
        BigDecimal amount = Validator.parsePositiveAmount(amountStr, "Withdrawal amount");

        Account account = accountDAO.getAccountByNumber(accountNumber);
        ensureActive(account);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in " + accountNumber + ": available " +
                            account.getBalance() + ", requested " + amount);
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        accountDAO.updateBalance(account.getId(), newBalance);

        Transaction txn = new Transaction(account.getId(), TransactionType.WITHDRAWAL, amount,
                newBalance, null, LocalDateTime.now());
        transactionDAO.addTransaction(txn);
        return txn;
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, String amountStr)
            throws InvalidInputException, AccountNotFoundException, AccountClosedException,
            InsufficientBalanceException, SQLException {

        Validator.requireNonEmpty(fromAccountNumber, "From account number");
        Validator.requireNonEmpty(toAccountNumber, "To account number");
        if (fromAccountNumber.trim().equalsIgnoreCase(toAccountNumber.trim())) {
            throw new InvalidInputException("Cannot transfer to the same account.");
        }
        BigDecimal amount = Validator.parsePositiveAmount(amountStr, "Transfer amount");

        Account fromAccount = accountDAO.getAccountByNumber(fromAccountNumber);
        Account toAccount = accountDAO.getAccountByNumber(toAccountNumber);
        ensureActive(fromAccount);
        ensureActive(toAccount);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in " + fromAccountNumber + ": available " +
                            fromAccount.getBalance() + ", requested " + amount);
        }

        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
        BigDecimal toNewBalance = toAccount.getBalance().add(amount);

        accountDAO.updateBalance(fromAccount.getId(), fromNewBalance);
        accountDAO.updateBalance(toAccount.getId(), toNewBalance);

        LocalDateTime now = LocalDateTime.now();
        transactionDAO.addTransaction(new Transaction(fromAccount.getId(), TransactionType.TRANSFER_OUT,
                amount, fromNewBalance, toAccount.getAccountNumber(), now));
        transactionDAO.addTransaction(new Transaction(toAccount.getId(), TransactionType.TRANSFER_IN,
                amount, toNewBalance, fromAccount.getAccountNumber(), now));
    }

    public List<Transaction> getStatement(String accountNumber) throws AccountNotFoundException, SQLException {
        Account account = accountDAO.getAccountByNumber(accountNumber);
        return transactionDAO.getTransactionsForAccount(account.getId());
    }

    public List<Transaction> listAllTransactions() throws SQLException {
        return transactionDAO.getAllTransactions();
    }

    // ---------- Helpers ----------

    private void ensureActive(Account account) throws AccountClosedException {
        if (!account.isActive()) {
            throw new AccountClosedException("Account " + account.getAccountNumber() + " is closed.");
        }
    }

    /** Generates a pseudo-unique account number. In production, use a proper sequence. */
    private String generateAccountNumber() {
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "AC" + random;
    }
}
