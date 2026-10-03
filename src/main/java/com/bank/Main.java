package com.bank;

import com.bank.exception.BankException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.service.BankingService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point. Handles user interaction and delegates all real
 * work to BankingService. Every operation is wrapped in try/catch so
 * bad input or DB errors never crash the app — they print a clear message
 * and return to the menu.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BankingService service = new BankingService();

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   BANKING MANAGEMENT SYSTEM");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addCustomer();
                    case "2" -> listCustomers();
                    case "3" -> deleteCustomer();
                    case "4" -> openAccount();
                    case "5" -> listAllAccounts();
                    case "6" -> listAccountsForCustomer();
                    case "7" -> deposit();
                    case "8" -> withdraw();
                    case "9" -> transfer();
                    case "10" -> statement();
                    case "11" -> closeAccount();
                    case "12" -> listAllTransactions();
                    case "0" -> {
                        running = false;
                        System.out.println("Goodbye!");
                    }
                    default -> System.out.println("Invalid choice. Please select a valid menu number.");
                }
            } catch (BankException e) {
                // All domain/validation errors surface a clean message here.
                System.out.println("Error: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n----------------- MENU -----------------");
        System.out.println(" 1. Add Customer");
        System.out.println(" 2. List All Customers");
        System.out.println(" 3. Delete Customer");
        System.out.println(" 4. Open Account");
        System.out.println(" 5. List All Accounts");
        System.out.println(" 6. List Accounts for a Customer");
        System.out.println(" 7. Deposit");
        System.out.println(" 8. Withdraw");
        System.out.println(" 9. Transfer");
        System.out.println("10. Account Statement");
        System.out.println("11. Close Account");
        System.out.println("12. List All Transactions");
        System.out.println(" 0. Exit");
        System.out.print("Enter choice: ");
    }

    private static String prompt(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    private static void addCustomer() throws Exception {
        String name = prompt("Name");
        String email = prompt("Email");
        String phone = prompt("Phone (10 digits)");
        Customer customer = service.addCustomer(name, email, phone);
        System.out.println("Customer added successfully: " + customer);
    }

    private static void listCustomers() throws SQLException {
        List<Customer> customers = service.listAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers registered yet.");
            return;
        }
        customers.forEach(System.out::println);
    }

    private static void deleteCustomer() throws Exception {
        String id = prompt("Customer ID to delete");
        service.deleteCustomer(id);
        System.out.println("Customer deleted successfully.");
    }

    private static void openAccount() throws Exception {
        String customerId = prompt("Customer ID");
        String type = prompt("Account type (SAVINGS/CURRENT)");
        String initialBalance = prompt("Initial deposit amount (0 is allowed)");
        Account account = service.openAccount(customerId, type, initialBalance);
        System.out.println("Account opened successfully: " + account);
    }

    private static void listAllAccounts() throws SQLException {
        List<Account> accounts = service.listAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts opened yet.");
            return;
        }
        accounts.forEach(System.out::println);
    }

    private static void listAccountsForCustomer() throws Exception {
        String customerId = prompt("Customer ID");
        List<Account> accounts = service.listAccountsForCustomer(customerId);
        if (accounts.isEmpty()) {
            System.out.println("This customer has no accounts.");
            return;
        }
        accounts.forEach(System.out::println);
    }

    private static void deposit() throws Exception {
        String accNum = prompt("Account number");
        String amount = prompt("Deposit amount");
        Transaction txn = service.deposit(accNum, amount);
        System.out.println("Deposit successful. New balance: " + txn.getBalanceAfter());
    }

    private static void withdraw() throws Exception {
        String accNum = prompt("Account number");
        String amount = prompt("Withdrawal amount");
        Transaction txn = service.withdraw(accNum, amount);
        System.out.println("Withdrawal successful. New balance: " + txn.getBalanceAfter());
    }

    private static void transfer() throws Exception {
        String fromAcc = prompt("From account number");
        String toAcc = prompt("To account number");
        String amount = prompt("Transfer amount");
        service.transfer(fromAcc, toAcc, amount);
        System.out.println("Transfer of " + amount + " from " + fromAcc + " to " + toAcc + " completed.");
    }

    private static void statement() throws Exception {
        String accNum = prompt("Account number");
        List<Transaction> txns = service.getStatement(accNum);
        if (txns.isEmpty()) {
            System.out.println("No transactions for this account yet.");
            return;
        }
        txns.forEach(System.out::println);
    }

    private static void closeAccount() throws Exception {
        String accNum = prompt("Account number to close");
        service.closeAccount(accNum);
        System.out.println("Account " + accNum + " closed successfully.");
    }

    private static void listAllTransactions() throws SQLException {
        List<Transaction> txns = service.listAllTransactions();
        if (txns.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        txns.forEach(System.out::println);
    }
}
