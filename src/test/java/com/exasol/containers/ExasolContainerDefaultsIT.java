package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer.NoDriverFoundException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("slow")
// This test case is kept separate because it requires an unmodified container for the test cases.
@Testcontainers
class ExasolContainerDefaultsIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withRequiredServices();

    @Test
    void testGetDefaultUsername() {
        assertThat(CONTAINER.getUsername(), equalTo("SYS"));
    }

    @Test
    void testGetDefaultUser() {
        final String expectedUser = "JohnSmith";
        assertThat(CONTAINER.withUsername(expectedUser).getUsername(), equalTo(expectedUser));
    }

    // [itest->dsn~default-jdbc-connection-with-sys-credentials~1]
    @Test
    void testDefaultJdbcConnectionBelongsToSysUser() throws NoDriverFoundException, SQLException {
        try (final Connection connection = CONTAINER.createConnection("")) {
            try (final Statement statement = connection.createStatement()) {
                try (final ResultSet resultSet = statement.executeQuery("SELECT CURRENT_USER")) {
                    if (resultSet.next()) {
                        assertThat(resultSet.getString(1), equalTo("SYS"));
                    } else {
                        fail("Empty resultset trying to determine if user of default connection is SYS.");
                    }
                }
            }
        }
    }

    @Test
    void testGetDockerNetworkInternalIpAddressReturnsLocalhostWithDefaultBridge() {
        assertThat(CONTAINER.getDockerNetworkInternalIpAddress(), equalTo("127.0.0.1"));
    }
}