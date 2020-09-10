package com.exasol.containers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class purges all objects from an Exasol database.
 */
// [impl->dsn~purging~1]
class ExasolDatabaseCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolDatabaseCleaner.class);
    private final Statement statement;

    public ExasolDatabaseCleaner(final Statement statement) {
        this.statement = statement;
    }

    /**
     * Drop all existing database objects
     *
     * @throws SQLException if failed to delete an object
     */
    public void purge() throws SQLException {
        purgeObjects();
        purgeConnections();
        purgeUsers();
        purgeRoles();
    }

    private void purgeConnections() throws SQLException {
        try (final ResultSet resultSet = this.statement
                .executeQuery("SELECT CONNECTION_NAME FROM EXA_ALL_CONNECTIONS;")) {
            while (resultSet.next()) {
                final String connectionName = resultSet.getString("CONNECTION_NAME");
                final String dropCommand = "DROP CONNECTION IF EXISTS \"" + connectionName + "\"";
                LOGGER.debug(dropCommand);
                this.statement.executeUpdate(dropCommand);
            }
        }
    }

    private void purgeObjects() throws SQLException {
        try (final ResultSet resultSet = this.statement.executeQuery(
                "SELECT OBJECT_NAME, OBJECT_TYPE, OBJECT_IS_VIRTUAL FROM SYS.EXA_ALL_OBJECTS WHERE OWNER = 'SYS' ORDER BY CREATED DESC;")) {
            while (resultSet.next()) {
                final String objectName = resultSet.getString("OBJECT_NAME");
                final String objectType = (resultSet.getBoolean("OBJECT_IS_VIRTUAL") ? "VIRTUAL " : "")
                        + resultSet.getString("OBJECT_TYPE");
                dropObject(objectName, objectType);
            }
        }
    }

    private void dropObject(final String objectName, final String objectType) throws SQLException {
        if (objectType.equals("VIRTUAL TABLE") || objectType.equals("TABLE") || objectType.equals("SCRIPT")) {
            return;
        }
        final StringBuilder dropCommandBuilder = new StringBuilder("DROP ");
        if (objectType.equals("VIRTUAL SCHEMA")) {
            dropCommandBuilder.append("FORCE ");
        }
        dropCommandBuilder.append(objectType).append(" IF EXISTS \"").append(objectName).append("\"");
        if (objectType.equals("SCHEMA") || objectType.equals("VIRTUAL SCHEMA")) {
            dropCommandBuilder.append(" CASCADE");
        }
        final String dropCommand = dropCommandBuilder.toString();
        LOGGER.debug(dropCommand);
        this.statement.executeUpdate(dropCommand);
    }

    private void purgeUsers() throws SQLException {
        try (final ResultSet resultSet = this.statement.executeQuery("SELECT USER_NAME FROM EXA_ALL_USERS;")) {
            while (resultSet.next()) {
                final String userName = resultSet.getString("USER_NAME");
                if (!userName.equals("SYS")) {
                    final String dropCommand = "DROP USER \"" + userName + "\"";
                    LOGGER.debug(dropCommand);
                    this.statement.executeUpdate(dropCommand);
                }
            }
        }
    }

    private void purgeRoles() throws SQLException {
        final List<String> builtInRoles = List.of("PUBLIC", "DBA");
        try (final ResultSet resultSet = this.statement.executeQuery("SELECT ROLE_NAME FROM EXA_ALL_ROLES;")) {
            while (resultSet.next()) {
                final String roleName = resultSet.getString("ROLE_NAME");
                if (!builtInRoles.contains(roleName)) {
                    final String dropCommand = "DROP ROLE \"" + roleName + "\"";
                    LOGGER.debug(dropCommand);
                    this.statement.executeUpdate(dropCommand);
                }
            }
        }
    }
}
