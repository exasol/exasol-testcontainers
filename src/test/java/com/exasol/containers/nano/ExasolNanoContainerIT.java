package com.exasol.containers.nano;

import static com.exasol.containers.nano.ExasolNanoContainer.EXASOL_NANO_WEB_UI_PORT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.sql.*;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.*;

@Tag("slow")
class ExasolNanoContainerIT {
    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void startsAndProvidesJdbcConnection() {
        try (final ExasolNanoContainer container = new ExasolNanoContainer()) {
            container.start();

            assertAll(
                    () -> assertThat(container.getMappedPort(EXASOL_NANO_WEB_UI_PORT), greaterThan(0)),
                    () -> verifyDefaultConnection(container),
                    () -> verifyDefaultConnectionWithArg(container));
        }
    }

    private void verifyDefaultConnection(final ExasolNanoContainer container) throws SQLException {
        try (final Connection connection = container.createConnection();
                final Statement statement = connection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            assertThat(resultSet.next(), equalTo(true));
            assertThat(resultSet.getInt(1), equalTo(1));
        }
    }

    private void verifyDefaultConnectionWithArg(final ExasolNanoContainer container) throws SQLException {
        try (final Connection connection = container.createConnection(";autocommit=0");
                final Statement statement = connection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            assertThat(resultSet.next(), equalTo(true));
            assertThat(resultSet.getInt(1), equalTo(1));
        }
    }
}
