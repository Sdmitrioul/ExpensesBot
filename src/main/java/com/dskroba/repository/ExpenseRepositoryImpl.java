package com.dskroba.repository;

import com.dskroba.base.type.Pair;
import com.dskroba.type.AppUser;
import com.dskroba.type.Expense;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
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
                             INSERT INTO expenses (amount, description, created_at, type) VALUES (?, ?, ?, ?)
                     """)) {
            for (Expense expense : expenses) {
                insertQuery.setDouble(1, expense.amount());
                insertQuery.setString(2, expense.getNote());
                insertQuery.setTimestamp(3, new Timestamp(expense.date().toInstant().toEpochMilli()));
                insertQuery.setString(4, expense.getTag().name());
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
    public List<Expense> getExpenses(Pair<Instant, Instant> timeInterval) {
        var result = new ArrayList<Expense>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectQuery = connection.prepareStatement("""
                             SELECT amount, description, created_at, type FROM expenses WHERE created_at BETWEEN ? AND ?
                     """)) {
            selectQuery.setTimestamp(1, Timestamp.from(timeInterval.first()));
            selectQuery.setTimestamp(2, Timestamp.from(timeInterval.second()));
            try (ResultSet resultSet = selectQuery.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new Expense.Builder().amount(resultSet.getDouble(1))
                            .description(resultSet.getString(2))
                            .date(Date.from(resultSet.getTimestamp(3).toInstant()))
                            .setType(resultSet.getString(4))
                            .build());
                }
            }
        } catch (SQLException e) {
            log.error("Error while inserting expense: ", e);
        }
        return result;
    }

    @Override
    public AppUser addUser(AppUser user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertQuery = connection.prepareStatement("""
                             INSERT INTO tg_users (username, chat_id) VALUES (?, ?) RETURNING id
                     """)) {
            insertQuery.setString(1, user.name());
            insertQuery.setLong(2, user.chatId());
            insertQuery.execute();
            try (ResultSet possibleUser = insertQuery.getResultSet()) {
                if (possibleUser.next()) {
                    int id = possibleUser.getInt(1);
                    return new AppUser.Builder().setId(id).setName(user.name()).setChatId(user.chatId()).build();
                }
            }
        } catch (SQLException e) {
            log.error("Error while inserting new tg user: ", e);
        }
        return user;
    }

    @Override
    public List<AppUser> getAllUser() {
        List<AppUser> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectQuery = connection.prepareStatement("SELECT id, username, chat_id FROM tg_users");
             ResultSet possibleUsers = selectQuery.executeQuery()) {
            while (possibleUsers.next()) {
                users.add(new AppUser.Builder().setId(possibleUsers.getInt("id"))
                        .setName(possibleUsers.getString("username"))
                        .setChatId(possibleUsers.getLong("chat_id"))
                        .build());
            }
        } catch (SQLException e) {
            log.error("Error while inserting new tg user: ", e);
        }
        return users;
    }
}
