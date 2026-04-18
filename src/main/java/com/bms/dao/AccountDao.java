package com.bms.dao;

import com.bms.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class AccountDao {
    public Account save(Connection connection, Account account) throws SQLException {
        String sql = "INSERT INTO accounts(user_id, account_number, pin_hash, balance) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, account.getUserId());
            statement.setString(2, account.getAccountNumber());
            statement.setString(3, account.getPinHash());
            statement.setBigDecimal(4, account.getBalance());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    account.setId(keys.getLong(1));
                }
            }
        }

        return account;
    }

    public Optional<Account> findByUserId(Connection connection, long userId) throws SQLException {
        String sql = "SELECT id, user_id, account_number, pin_hash, balance, created_at FROM accounts WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Account> findByAccountNumber(Connection connection, String accountNumber) throws SQLException {
        String sql = "SELECT id, user_id, account_number, pin_hash, balance, created_at FROM accounts WHERE account_number = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public void updateBalance(Connection connection, long accountId, BigDecimal balance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, balance);
            statement.setLong(2, accountId);
            statement.executeUpdate();
        }
    }

    private Account map(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setId(resultSet.getLong("id"));
        account.setUserId(resultSet.getLong("user_id"));
        account.setAccountNumber(resultSet.getString("account_number"));
        account.setPinHash(resultSet.getString("pin_hash"));
        account.setBalance(resultSet.getBigDecimal("balance"));
        account.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        return account;
    }
}
