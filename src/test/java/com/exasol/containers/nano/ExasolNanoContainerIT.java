package com.exasol.containers.nano;

import static com.exasol.containers.nano.ExasolNanoContainer.EXASOL_NANO_WEB_UI_PORT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import java.sql.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("slow")
class ExasolNanoContainerIT {
    @Test
    void startsAndProvidesJdbcConnection() throws SQLException {
        try (final ExasolNanoContainer container = new ExasolNanoContainer()) {
            container.start();

            assertThat(container.getFirstMappedDatabasePort(), greaterThan(0));
            assertThat(container.getMappedPort(EXASOL_NANO_WEB_UI_PORT), greaterThan(0));

            try (final Connection connection = container.createConnection();
                    final Statement statement = connection.createStatement();
                    final ResultSet resultSet = statement.executeQuery("SELECT 1")) {
                assertThat(resultSet.next(), equalTo(true));
                assertThat(resultSet.getInt(1), equalTo(1));
            }
        }
    }
}
