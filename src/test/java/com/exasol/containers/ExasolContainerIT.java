package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.config.ClusterConfiguration;

// [itest->dsn~exasol-container-controls-docker-container~1]
// [itest->dsn~exasol-container-ready-criteria~3]

// This test contains test cases that modify the configuration of the container. Don't add test
// cases that depend on the default settings!
@Testcontainers
class ExasolContainerIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainerIT.class);

    @Container // [itest->dsn~exasol-container-starts-with-test~1]
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>(
            ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE) //
                    .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    // [itest->dsn~exasol-container-uses-privileged-mode~1]
    @Test
    void testContainerRunsInPrivilegedMode() {
        assertThat(container.isPrivilegedMode(), equalTo(true));
    }

    @Test
    void testGetDriverClassName() {
        assertThat(container.getDriverClassName(), equalTo("com.exasol.jdbc.EXADriver"));
    }

    @Test
    void testGetJdbcUrl() throws Exception {
        assertThat(container.getJdbcUrl(), matchesPattern("jdbc:exa:localhost:\\d{1,5}"));
    }

    @Test
    void testWithUsername() {
        final String expectedUsername = "JohnathanSmith";
        assertThat(container.withUsername(expectedUsername).getUsername(), equalTo(expectedUsername));
    }

    @Test
    void testWithPassword() {
        final String expectedPwd = "open sesame!";
        assertThat(container.withPassword(expectedPwd).getPassword(), equalTo(expectedPwd));
    }

    @Test
    void testGetClusterConfiguration() throws InterruptedException, ExasolContainerException {
        final ClusterConfiguration configuration = container.getClusterConfiguration();
        assertThat(configuration.getDefaultBucketReadPassword(), not(emptyOrNullString()));
    }

    // [itest->dsn~exasol-container-provides-a-jdbc-connection-for-username-and-password~1]
    @Test
    void testCreateConnectionForUser() throws SQLException {
        try (final Connection connection = container.createConnectionForUser(
                ExasolContainerConstants.DEFAULT_ADMIN_USER, ExasolContainerConstants.DEFAULT_SYS_USER_PASSWORD)) {
            assertThat("Connection established.", connection.isValid(5), equalTo(true));
        }
    }

    @Test
    void testGetLivenessCheckPortNumbers() {
        final Set<Integer> expectedPorts = Set
                .of(container.getMappedPort(ExasolContainerConstants.CONTAINER_INTERNAL_DATABASE_PORT));
        assertThat(container.getLivenessCheckPortNumbers(), equalTo(expectedPorts));
    }
}