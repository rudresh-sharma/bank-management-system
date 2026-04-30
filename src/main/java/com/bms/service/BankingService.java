package com.bms.service;

import com.bms.dao.AccountDao;
import com.bms.dao.TransactionDao;
import com.bms.exception.BankingException;
import com.bms.model.Account;
import com.bms.model.TransactionRecord;
import com.bms.util.AccountNumberGenerator;
import com.bms.util.ConnectionFactory;
import com.bms.util.SecurityUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class BankingService {
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    public BankingService() {
        this.accountDao = new AccountDao();
        this.transactionDao = new TransactionDao();
    }

    public Account createAccount(long userId, String pin) throws SQLException, BankingException {
        validatePin(pin);

        try (Connection connection = ConnectionFactory.getConnection()) {
            if (accountDao.findByUserId(connection, userId).isPresent()) {
                throw new BankingException("User already has a bank account.");
            }

            Account account = new Account();
            account.setUserId(userId);
            account.setAccountNumber(generateUniqueAccountNumber(connection));
            account.setPinHash(SecurityUtil.hashValue(pin));
            account.setBalance(BigDecimal.ZERO);
            return accountDao.save(connection, account);
        }
    }

    public Account getAccountByUserId(long userId) throws SQLException, BankingException {
        try (Connection connection = ConnectionFactory.getConnection()) {
            return getExistingAccount(connection, userId);
        }
    }

    public BigDecimal deposit(long userId, BigDecimal amount, String pin) throws SQLException, BankingException {
        validateAmount(amount);

        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Account account = getExistingAccount(connection, userId);
                verifyPin(account, pin);

                BigDecimal updatedBalance = account.getBalance().add(amount);
                accountDao.updateBalance(connection, account.getId(), updatedBalance);
                transactionDao.save(connection, createRecord(account.getId(), "DEPOSIT", amount, null, "Cash deposit"));

                connection.commit();
                return updatedBalance;
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public BigDecimal withdraw(long userId, BigDecimal amount, String pin) throws SQLException, BankingException {
        validateAmount(amount);

        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Account account = getExistingAccount(connection, userId);
                verifyPin(account, pin);

                if (account.getBalance().compareTo(amount) < 0) {
                    throw new BankingException("Insufficient balance.");
                }

                BigDecimal updatedBalance = account.getBalance().subtract(amount);
                accountDao.updateBalance(connection, account.getId(), updatedBalance);
                transactionDao.save(connection, createRecord(account.getId(), "WITHDRAW", amount, null, "Cash withdrawal"));

                connection.commit();
                return updatedBalance;
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public BigDecimal transfer(long userId, String targetAccountNumber, BigDecimal amount, String pin)
            throws SQLException, BankingException {
        validateAmount(amount);
        if (targetAccountNumber == null || targetAccountNumber.isBlank()) {
            throw new BankingException("Recipient account number is required.");
        }

        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Account sender = getExistingAccount(connection, userId);
                verifyPin(sender, pin);

                Account receiver = accountDao.findByAccountNumber(connection, targetAccountNumber.trim())
                        .orElseThrow(() -> new BankingException("Recipient account not found."));

                if (sender.getAccountNumber().equals(receiver.getAccountNumber())) {
                    throw new BankingException("Cannot transfer money to the same account.");
                }

                if (sender.getBalance().compareTo(amount) < 0) {
                    throw new BankingException("Insufficient balance.");
                }

                BigDecimal senderBalance = sender.getBalance().subtract(amount);
                BigDecimal receiverBalance = receiver.getBalance().add(amount);

                accountDao.updateBalance(connection, sender.getId(), senderBalance);
                accountDao.updateBalance(connection, receiver.getId(), receiverBalance);

                transactionDao.save(connection, createRecord(
                        sender.getId(), "TRANSFER_OUT", amount, receiver.getAccountNumber(), "Transfer to recipient"));
                transactionDao.save(connection, createRecord(
                        receiver.getId(), "TRANSFER_IN", amount, sender.getAccountNumber(), "Transfer received"));

                connection.commit();
                return senderBalance;
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public BigDecimal getBalance(long userId, String pin) throws SQLException, BankingException {
        try (Connection connection = ConnectionFactory.getConnection()) {
            Account account = getExistingAccount(connection, userId);
            verifyPin(account, pin);
            return account.getBalance();
        }
    }

    public void changePin(long userId, String currentPin, String newPin) throws SQLException, BankingException {
        try (Connection connection = ConnectionFactory.getConnection()) {
            Account account = getExistingAccount(connection, userId);
            verifyPin(account, currentPin);
            validatePin(newPin);

            if (SecurityUtil.matches(newPin, account.getPinHash())) {
                throw new BankingException("New PIN must be different from the current PIN.");
            }

            accountDao.updatePinHash(connection, account.getId(), SecurityUtil.hashValue(newPin));
        }
    }

    private String generateUniqueAccountNumber(Connection connection) throws SQLException {
        String accountNumber = AccountNumberGenerator.generate();

        while (accountDao.findByAccountNumber(connection, accountNumber).isPresent()) {
            accountNumber = AccountNumberGenerator.generate();
        }

        return accountNumber;
    }

    private Account getExistingAccount(Connection connection, long userId) throws SQLException, BankingException {
        return accountDao.findByUserId(connection, userId)
                .orElseThrow(() -> new BankingException("No bank account found for this user."));
    }

    private void validateAmount(BigDecimal amount) throws BankingException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Amount must be greater than zero.");
        }
    }

    private void validatePin(String pin) throws BankingException {
        if (pin == null || !pin.matches("\\d{4}")) {
            throw new BankingException("PIN must be exactly 4 digits.");
        }
    }

    private void verifyPin(Account account, String pin) throws BankingException {
        validatePin(pin);

        if (!SecurityUtil.matches(pin, account.getPinHash())) {
            throw new BankingException("Invalid PIN.");
        }
    }

    private TransactionRecord createRecord(
            long accountId, String type, BigDecimal amount, String referenceAccount, String description) {
        TransactionRecord record = new TransactionRecord();
        record.setAccountId(accountId);
        record.setTransactionType(type);
        record.setAmount(amount);
        record.setReferenceAccount(referenceAccount);
        record.setDescription(description);
        return record;
    }
}
