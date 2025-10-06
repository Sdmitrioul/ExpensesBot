package com.dskroba.expenses;

import com.dskroba.type.Timeframe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpenseRepositoryImpl implements ExpenseRepository {
    private static final Logger log = LogManager.getLogger(ExpenseRepositoryImpl.class);

    private final DataSource dataSource;

    public ExpenseRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean insertExpense(Expense expense) {
        return insertExpense(List.of(expense));
    }

    @Override
    public boolean insertExpense(List<Expense> expenses) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertQuery = connection.prepareStatement("""
                             INSERT INTO expenses (amount, issuer_id, description, created_at, type, currency) VALUES (?, ?, ?, ?, ?, ?)
                     """)) {
            for (Expense expense : expenses) {
                insertQuery.setDouble(1, expense.amount());
                insertQuery.setLong(2, expense.issuer());
                insertQuery.setString(3, expense.description());
                insertQuery.setTimestamp(4, new Timestamp(expense.date().toInstant().toEpochMilli()));
                insertQuery.setString(5, expense.expenseTag().name());
                insertQuery.setString(6, expense.currency().name());
                insertQuery.addBatch();
            }
            insertQuery.executeBatch();
            return true;
        } catch (SQLException e) {
            log.error("Error while inserting expense: ", e);
        }
        return false;
    }

    @Override
    public List<Expense> getExpenses(Timeframe timeframe, Long issuerId) {
        var result = new ArrayList<Expense>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectQuery = connection.prepareStatement("""
                             SELECT id, amount, issuer_id, description, created_at, type, currency
                             FROM expenses
                             WHERE created_at BETWEEN ? AND ?
                                 AND issuer_id = ?
                     """)) {
            selectQuery.setTimestamp(1, Timestamp.from(timeframe.from()));
            selectQuery.setTimestamp(2, Timestamp.from(timeframe.to()));
            selectQuery.setLong(3, issuerId);
            try (ResultSet resultSet = selectQuery.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new Expense.Builder()
                            .id(resultSet.getLong(1))
                            .amount(resultSet.getDouble(2))
                            .issuerId(resultSet.getLong(3))
                            .description(resultSet.getString(4))
                            .date(Date.from(resultSet.getTimestamp(5).toInstant()))
                            .type(resultSet.getString(6))
                            .currency(resultSet.getString(7))
                            .build());
                }
            }
        } catch (SQLException e) {
            log.error("Error while inserting expense: ", e);
        }
        return result;
    }
}
