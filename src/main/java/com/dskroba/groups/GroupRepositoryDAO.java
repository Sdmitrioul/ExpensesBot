package com.dskroba.groups;

import com.dskroba.base.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class GroupRepositoryDAO implements GroupRepository {
    private static final Logger log = LoggerFactory.getLogger(GroupRepositoryDAO.class);
    private final DataSource dataSource;

    public GroupRepositoryDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Group> getPrincipalGroups(Long principalId) throws RepositoryException {
        return connectionProvider(connection -> {
            try (PreparedStatement select = connection.prepareStatement("""
                    WITH groups_ids AS (
                        SELECT gp.group_id AS value
                        FROM group_participants gp
                        WHERE gp.principal_id = ?
                        UNION
                        SELECT g.id AS value
                        FROM groups g
                        WHERE g.owner_id = ?)
                    SELECT g.*,
                           COALESCE(ARRAY_AGG(gp.principal_id), ARRAY[]::bigint[]) AS participant_ids
                    FROM groups g
                        LEFT JOIN group_participants gp
                            ON g.id = gp.group_id
                    WHERE g.id IN (SELECT value FROM groups_ids)
                    GROUP BY g.id;
                    """)) {
                select.setLong(1, principalId);
                select.setLong(2, principalId);
                try (ResultSet resultSet = select.executeQuery()) {
                    List<Group> groups = new ArrayList<>();
                    while (resultSet.next()) {
                        groups.add(addGroupParticipants(
                                resultSet.getArray("participant_ids"), new Group.Builder())
                                .id(resultSet.getLong("id"))
                                .name(resultSet.getString("name"))
                                .ownerId(resultSet.getLong("owner_id"))
                                .build());
                    }
                    return groups;
                }
            } catch (SQLException e) {
                String message = "Error while getting groups for principal %s".formatted(principalId);
                log.error(message, e);
                throw new RepositoryException(message);
            }
        });
    }

    @Override
    public Group saveGroup(Group group) throws RepositoryException {
        return connectionProvider(connection -> {
            if (group.id() == null) {
                Group createdGroup = craeteGroup(group, connection);
                saveGroupParticipants(createdGroup.id(), createdGroup.members(), connection);
                return createdGroup;
            }
            Group existingGroup = getGroup(group.id(), connection);
            if (existingGroup == null) {
                String message = "No group found for id %s".formatted(group.id());
                log.error(message);
                throw new RepositoryException(message);
            }
            if (!group.haveSameMembers(existingGroup)) {
                deleteGroupParticipants(existingGroup.id(), connection);
                saveGroupParticipants(existingGroup.id(), group.members(), connection);
            }
            if (existingGroup.hasDiff(group)) {
                updateGroup(group, connection);
            }
            return group;
        });
    }

    @Override
    public void deleteGroup(Long groupId) throws RepositoryException {
        connectionProvider(connection -> {
            deleteGroupParticipants(groupId, connection);
            deleteGroup(groupId, connection);
        });
    }

    @Override
    public void leaveGroup(Long groupId, Long principalId) throws RepositoryException {
        connectionProvider(connection -> {
            int result = deleteParticipants(List.of(new Participant(principalId, groupId)), connection);
            if (result == 0) {
                log.warn("No group participant found for group {} and principal {}", groupId, principalId);
            }
        });
    }

    private void connectionProvider(Consumer<Connection> consumer) throws RepositoryException {
        connectionProvider(connection -> {
            consumer.accept(connection);
            return null;
        });
    }

    private <R> R connectionProvider(Function<Connection, R> function) throws RepositoryException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                return function.apply(connection);
            } catch (RepositoryException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            String message = "Error while getting connection";
            log.error(message, e);
            throw new RepositoryException(message);
        }
    }

    private static Group.Builder addGroupParticipants(Array participants, Group.Builder builder) throws SQLException {
        if (participants != null) {
            Long[] ids = (Long[]) participants.getArray();
            for (Long id : ids) {
                builder.addMember(id);
            }
        }
        return builder;
    }

    private static void updateGroup(Group group, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE groups SET name = ?, owner_id = ? WHERE id = ?")) {
            preparedStatement.setString(1, group.name());
            preparedStatement.setLong(2, group.owner());
            preparedStatement.setLong(3, group.id());
            if (preparedStatement.executeUpdate() != 1) {
                String message = "No group found for id %s".formatted(group.id());
                log.error(message);
                throw new RepositoryException(message);
            }
        } catch (SQLException e) {
            String message = "Error while updating group %s".formatted(group);
            log.error(message, e);
            throw new RepositoryException(message);
        }
    }

    private static void saveGroupParticipants(Long id, Set<Long> members, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO group_participants (group_id, principal_id) VALUES (?, ?)")) {
            for (Long memberId : members) {
                preparedStatement.setLong(1, id);
                preparedStatement.setLong(2, memberId);
                preparedStatement.addBatch();
            }
            int inserted = preparedStatement.executeUpdate();
            if (inserted == 0) {
                log.info("No participants inserted for group {}", id);
            }
            if (inserted != members.size()) {
                log.warn("Inserted {} participants for group {}, but expected {} {}", inserted, id, members.size(), members);
            }
        } catch (SQLException e) {
            String message = "Error while saving group participants for group %s"
                    .formatted(id);
            log.error(message, e);
            throw new RepositoryException(message);
        }
    }

    private static Group craeteGroup(Group group, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO groups (owner_id, name) VALUES (?, ?) RETURNING id")) {
            preparedStatement.setLong(1, group.owner());
            preparedStatement.setString(2, group.name());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Group.Builder.from(group).id(resultSet.getLong("id")).build();
                } else {
                    String message = "Error while inserting group %s".formatted(group);
                    log.error(message);
                    throw new RepositoryException(message);
                }
            }
        } catch (SQLException e) {
            String message = "Error while inserting group %s".formatted(group);
            log.error(message, e);
            throw new RepositoryException(message);
        }
    }

    private static Group getGroup(Long groupId, Connection connection) {
        try (PreparedStatement select = connection.prepareStatement("""
                SELECT g.*,
                       COALESCE(ARRAY_AGG(gp.principal_id), ARRAY[]::bigint[]) AS participant_ids
                FROM groups g
                    LEFT JOIN group_participants gp
                        ON g.id = gp.group_id
                WHERE g.id = ?
                GROUP BY g.id;
                """)) {
            select.setLong(1, groupId);
            try (ResultSet resultSet = select.executeQuery()) {
                if (resultSet.next()) {
                    return addGroupParticipants(
                            resultSet.getArray("participant_ids"), new Group.Builder())
                            .id(resultSet.getLong("id"))
                            .name(resultSet.getString("name"))
                            .ownerId(resultSet.getLong("owner_id"))
                            .build();
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            String message = "Error while getting group %s".formatted(groupId);
            log.error(message, e);
            throw new RepositoryException(message);
        }
    }

    private static void deleteGroup(Long groupId, Connection connection) {
        try (PreparedStatement deleteGroup = connection.prepareStatement("DELETE FROM groups WHERE id = ?")) {
            deleteGroup.setLong(1, groupId);
            int result = deleteGroup.executeUpdate();
            if (result == 0) {
                log.warn("No group found for id {}", groupId);
            }
        } catch (SQLException e) {
            String message = "Error while deleting group %s".formatted(groupId);
            log.error(message, e);
            throw new RepositoryException(message);
        }
    }

    private static void deleteGroupParticipants(Long groupId, Connection connection) {
        try (PreparedStatement deleteParticipants = connection.prepareStatement("""
                DELETE FROM group_participants
                       WHERE group_id = ?
                """)) {
            deleteParticipants.setLong(1, groupId);
            int result = deleteParticipants.executeUpdate();
            if (result == 0) {
                log.info("No participants found for group {}", groupId);
            }
        } catch (SQLException e) {
            String message = "Error while deleting group participants for group %s"
                    .formatted(groupId);
            log.error(message, e);
            throw new RepositoryException(message);
        }
    }

    private static int deleteParticipants(List<Participant> participants, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                DELETE FROM group_participants
                       WHERE group_id = ?
                         AND principal_id = ?""")) {
            for (Participant participant : participants) {
                preparedStatement.setLong(1, participant.groupId());
                preparedStatement.setLong(2, participant.principalId());
                preparedStatement.addBatch();
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String message = "Error while deleting group participants: " + participants;
            log.error(message, e);
            throw new RepositoryException(message);
        }
    }

    private record Participant(Long principalId, Long groupId) {
    }
}
