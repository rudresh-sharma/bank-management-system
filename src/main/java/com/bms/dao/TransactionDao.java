package com.bms.dao;

import com.bms.model.TransactionRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDao {
    public void save(Connection connection, TransactionRecord transactionRecord) throws SQLException {
        String sql = """
                INSERT INTO transactions(account_id, transaction_type, amount, reference_account, description)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, transactionRecord.getAccountId());
            statement.setString(2, transactionRecord.getTransactionType());
            statement.setBigDecimal(3, transactionRecord.getAmount());
            statement.setString(4, transactionRecord.getReferenceAccount());
            statement.setString(5, transactionRecord.getDescription());
            statement.executeUpdate();
        }
    }
}
