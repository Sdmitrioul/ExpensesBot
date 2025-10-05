package com.dskroba.principals;

import com.dskroba.type.Currency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class PrincipalRepositoryDAO implements PrincipalRepository {
    private static final Logger log = LogManager.getLogger(PrincipalRepositoryDAO.class);

    private final DataSource dataSource;

    public PrincipalRepositoryDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Principal savePrincipal(Principal principal) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertQuery = connection.prepareStatement("""
                             INSERT INTO principals (telegram_id, username, chat_id, currency)
                             VALUES (?, ?, ?, ?)
                             ON CONFLICT (telegram_id)
                             DO UPDATE SET
                                 username = EXCLUDED.username,
                                 chat_id = EXCLUDED.chat_id,
                                 currency = EXCLUDED.currency
                             RETURNING id
                     """)) {
            String code = principal.code();
            Long chatId = principal.chatId();
            Currency currency = principal.currency();
            insertQuery.setLong(1, principal.key().telegramId());
            insertQuery.setString(2, code);
            insertQuery.setLong(3, chatId);
            insertQuery.setString(4, currency.name());
            try (ResultSet rs = insertQuery.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    return Principal.Builder
                            .from(principal)
                            .id(id)
                            .build();
                }
            }
        } catch (SQLException e) {
            log.error("Error while inserting new tg user: ", e);
        }
        return principal;
    }

    @Override
    public Principal getByTelegramId(Long telegramId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     SELECT id, telegram_id, username, chat_id, currency
                     FROM principals
                     WHERE telegram_id = ?
                     """)) {
            preparedStatement.setLong(1, telegramId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPrincipal(resultSet);
                }
            }
        } catch (SQLException e) {
            log.error("Problem during getting principal by TG ID, {}", telegramId, e);
        }
        return null;
    }

    @Override
    public List<Principal> getPrincipals() {
        List<Principal> principals = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectQuery = connection.prepareStatement("SELECT id, telegram_id, username, chat_id, currecny FROM principals");
             ResultSet resultSet = selectQuery.executeQuery()) {
            while (resultSet.next()) {
                principals.add(mapPrincipal(resultSet));
            }
        } catch (SQLException e) {
            log.error("Error while inserting new tg user: ", e);
        }
        return principals;
    }

    private static Principal mapPrincipal(ResultSet resultSet) throws SQLException {
        return new Principal.Builder()
                .id(resultSet.getLong("id"))
                .telegramId(resultSet.getLong("telegram_id"))
                .code(resultSet.getString("username"))
                .chatId(resultSet.getLong("chat_id"))
                .currency(resultSet.getString("currency"))
                .build();
    }
}
