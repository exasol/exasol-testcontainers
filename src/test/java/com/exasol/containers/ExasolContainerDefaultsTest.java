package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer.NoDriverFoundException;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// This test case is kept separate because it requires an unmodified container for the test cases.
@Testcontainers
class ExasolContainerDefaultsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainerTest.class);

    @Container
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>(
            ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE);

    @BeforeAll
    static void beforeAll() {
        container.followOutput(new Slf4jLogConsumer(LOGGER));
    }

    @Test
    void testGetDefaultUsername() {
        assertThat(container.getUsername(), equalTo("SYS"));
    }

    @Test
    void testGetDefaultUser() {
        final String expectedUser = "JohnSmith";
        assertThat(container.withUsername(expectedUser).getUsername(), equalTo(expectedUser));
    }

    // [itest->dsn~default-jdbc-connection-with-sys-credentials~1]
    @Test
    void testDefaultJdbcConnectionBelongsToSysUser() throws NoDriverFoundException, SQLException {
        try (final Connection connection = container.createConnection("")) {
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
}