package com.exasol.containers;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.*;

import org.junit.jupiter.api.Test;

class ReusedContainerIT {
    @Test
    void testReuseContainer() throws SQLException {
        try (ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()) {
            container.withReuse(true).start();
            final Connection connection = container.createConnection();
            final Statement statement = connection.createStatement();
            statement.execute("CREATE SCHEMA REUSE_CHECK_SCHEMA");
            statement.execute("CREATE TABLE REUSE_CHECK_SCHEMA.T (C1 BOOLEAN)");
            container.stop();
        }
        try (ExasolContainer<? extends ExasolContainer<?>> reusedContainer = new ExasolContainer<>()) {
            reusedContainer.withReuse(true).start();
            final Connection connection = reusedContainer.createConnection();
            final Statement statement = connection.createStatement();
            try {
                statement.executeQuery("DESCRIBE REUSE_CHECK_SCHMEA.T");
            } catch (final SQLException exception) {
                fail("Table created in previous container does not exist in reused container.\nCaused by: "
                        + exception.getMessage());
            } finally {
                reusedContainer.stop();
            }
        }
    }
}