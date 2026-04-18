package com.bms;

import com.bms.exception.BankingException;
import com.bms.model.Account;
import com.bms.model.User;
import com.bms.service.AuthService;
import com.bms.service.BankingService;
import com.bms.util.ConsoleHelper;
import com.bms.util.SchemaInitializer;

import java.math.BigDecimal;
import java.sql.SQLException;

public class Main {
    private final AuthService authService;
    private final BankingService bankingService;
    private final ConsoleHelper console;

    public Main() {
        this.authService = new AuthService();
        this.bankingService = new BankingService();
        this.console = new ConsoleHelper();
    }

    public static void main(String[] args) {
        try {
            SchemaInitializer.initialize();
            new Main().start();
        } catch (SQLException ex) {
            System.out.println("Unable to initialize the database: " + ex.getMessage());
        }
    }

    private void start() {
        boolean running = true;

        while (running) {
            printWelcome();
            int choice = console.readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> register();
                    case 2 -> login();
                    case 3 -> running = false;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (BankingException | SQLException ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }

        System.out.println("Thank you for using Banking Management System.");
    }

    private void printWelcome() {
        System.out.println();
        System.out.println("=== Banking Management System ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
    }

    private void register() throws SQLException, BankingException {
        System.out.println();
        System.out.println("=== User Registration ===");

        String fullName = console.readRequiredLine("Full name: ");
        String email = console.readRequiredLine("Email: ");
        String password = console.readSecret("Password: ");

        User user = authService.registerUser(fullName, email, password);
        System.out.println("Registration successful for " + user.getFullName() + ".");

        if (console.readYesNo("Create a bank account now? (y/n): ")) {
            createAccount(user);
        }
    }

    private void login() throws SQLException, BankingException {
        System.out.println();
        System.out.println("=== User Login ===");

        String email = console.readRequiredLine("Email: ");
        String password = console.readSecret("Password: ");

        User user = authService.login(email, password);
        System.out.println("Welcome, " + user.getFullName() + "!");
        showDashboard(user);
    }

    private void showDashboard(User user) throws SQLException, BankingException {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("=== Dashboard ===");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Check Balance");
            System.out.println("6. View Account Details");
            System.out.println("7. Logout");

            int choice = console.readInt("Choose an option: ");

            switch (choice) {
                case 1 -> createAccount(user);
                case 2 -> deposit(user);
                case 3 -> withdraw(user);
                case 4 -> transfer(user);
                case 5 -> checkBalance(user);
                case 6 -> showAccountDetails(user);
                case 7 -> loggedIn = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void createAccount(User user) throws SQLException, BankingException {
        System.out.println();
        System.out.println("=== Account Creation ===");

        String pin = console.readPin("Create 4-digit PIN: ");
        Account account = bankingService.createAccount(user.getId(), pin);
        System.out.println("Account created successfully.");
        System.out.println("Account Number: " + account.getAccountNumber());
    }

    private void deposit(User user) throws SQLException, BankingException {
        System.out.println();
        System.out.println("=== Deposit Money ===");

        BigDecimal amount = console.readAmount("Deposit amount: ");
        String pin = console.readPin("Enter PIN: ");
        BigDecimal updatedBalance = bankingService.deposit(user.getId(), amount, pin);

        System.out.println("Deposit successful. Updated balance: " + updatedBalance);
    }

    private void withdraw(User user) throws SQLException, BankingException {
        System.out.println();
        System.out.println("=== Withdraw Money ===");

        BigDecimal amount = console.readAmount("Withdrawal amount: ");
        String pin = console.readPin("Enter PIN: ");
        BigDecimal updatedBalance = bankingService.withdraw(user.getId(), amount, pin);

        System.out.println("Withdrawal successful. Updated balance: " + updatedBalance);
    }

    private void transfer(User user) throws SQLException, BankingException {
        System.out.println();
        System.out.println("=== Transfer Money ===");

        String targetAccountNumber = console.readRequiredLine("Recipient account number: ");
        BigDecimal amount = console.readAmount("Transfer amount: ");
        String pin = console.readPin("Enter PIN: ");

        BigDecimal updatedBalance = bankingService.transfer(user.getId(), targetAccountNumber, amount, pin);
        System.out.println("Transfer successful. Updated balance: " + updatedBalance);
    }

    private void checkBalance(User user) throws SQLException, BankingException {
        System.out.println();
        System.out.println("=== Balance Inquiry ===");

        String pin = console.readPin("Enter PIN: ");
        BigDecimal balance = bankingService.getBalance(user.getId(), pin);
        System.out.println("Current balance: " + balance);
    }

    private void showAccountDetails(User user) throws SQLException, BankingException {
        Account account = bankingService.getAccountByUserId(user.getId());
        System.out.println();
        System.out.println("=== Account Details ===");
        System.out.println("Account Number: " + account.getAccountNumber());
        System.out.println("Balance: " + account.getBalance());
        System.out.println("Created At: " + account.getCreatedAt());
    }
}
