package com.dskroba.expenses;

import com.dskroba.type.Currency;
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
                             INSERT INTO expenses (type, amount, currency, description, issuer_id, group_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)
                     """)) {
            for (Expense expense : expenses) {
                insertQuery.setString(1, expense.expenseType().name());
                insertQuery.setDouble(2, expense.amount());
                insertQuery.setString(3, expense.currency().name());
                insertQuery.setString(4, expense.description());
                insertQuery.setLong(5, expense.issuer());
                insertQuery.setLong(6, expense.group());
                insertQuery.setTimestamp(7, new Timestamp(expense.date().toInstant().toEpochMilli()));
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
    public List<Expense> getExpenses(Timeframe timeframe, Long groupId, Currency currency) {
        var result = new ArrayList<Expense>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectQuery = connection.prepareStatement("""
                             SELECT id, type, amount, currency, description, issuer_id, group_id, created_at
                             FROM expenses
                             WHERE created_at BETWEEN ? AND ?
                                 AND group_id = ?
                                 AND currency = ?
                             ORDER BY created_at DESC
                     """)) {
            selectQuery.setTimestamp(1, Timestamp.from(timeframe.from()));
            selectQuery.setTimestamp(2, Timestamp.from(timeframe.to()));
            selectQuery.setLong(3, groupId);
            selectQuery.setString(4, currency.name());
            try (ResultSet resultSet = selectQuery.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new Expense.Builder()
                            .id(resultSet.getLong(1))
                            .type(resultSet.getString(2))
                            .amount(resultSet.getDouble(3))
                            .currency(resultSet.getString(4))
                            .description(resultSet.getString(5))
                            .issuerId(resultSet.getLong(6))
                            .groupId(resultSet.getLong(7))
                            .date(Date.from(resultSet.getTimestamp(8).toInstant()))
                            .build());
                }
            }
        } catch (SQLException e) {
            log.error("Error while inserting expense: ", e);
        }
        return result;
    }
}
