package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.config.ClusterConfiguration;

// [itest->dsn~exasol-container-controls-docker-container~1]
// [itest->dsn~exasol-container-ready-criteria~3]

// This test contains test cases that modify the configuration of the container. Don't add test
// cases that depend on the default settings!
@Tag("slow")
@Testcontainers
class ExasolContainerIT {

    private static final Duration DEFAULT_JDBC_LOGIN_TIMEOUT = Duration.ofSeconds(10);
    @Container // [itest->dsn~exasol-container-starts-with-test~1]
    @SuppressWarnings("resource") // Will be closed by @Testcontainers
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withReuse(true).withRequiredServices(ExasolService.JDBC);

    // [itest->dsn~exasol-container-uses-privileged-mode~1]
    @Test
    void testContainerRunsInPrivilegedMode() {
        assertThat(CONTAINER.isPrivilegedMode(), equalTo(true));
    }

    @Test
    void testGetDriverClassName() {
        assertThat(CONTAINER.getDriverClassName(), equalTo("com.exasol.jdbc.EXADriver"));
    }

    @Test
    void testGetJdbcUrlContainsFingerprint() throws Exception {
        final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
        final String ipRegexp = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
        final String hostNamePattern = "(localhost|" + ipRegexp + ")";
        assertThat(CONTAINER.getJdbcUrl(), matchesPattern(
                "jdbc:exa:" + hostNamePattern + ":\\d{1,5};validateservercertificate=1;fingerprint=\\w{64};.*"));
    }

    @Test
    void testGetJdbcUrlContainsDefaultLoginTimeout() {
        assertThat(CONTAINER.getJdbcUrl(),
                containsString(";logintimeout=" + DEFAULT_JDBC_LOGIN_TIMEOUT.toMillis() + ";"));
    }

    @Test
    void testGetJdbcUrlContainsCustomLoginTimeout() {
        try {
            CONTAINER.withJdbcLoginTimeout(Duration.ofSeconds(3));
            assertThat(CONTAINER.getJdbcUrl(), containsString(";logintimeout=3000;"));
        } finally {
            CONTAINER.withJdbcLoginTimeout(DEFAULT_JDBC_LOGIN_TIMEOUT);
        }
    }

    @Test
    void testGetJdbcUrlWithoutLoginTimeout() {
        try {
            CONTAINER.withJdbcLoginTimeout(null);
            assertThat(CONTAINER.getJdbcUrl(), not(containsString("logintimeout")));
        } finally {
            CONTAINER.withJdbcLoginTimeout(DEFAULT_JDBC_LOGIN_TIMEOUT);
        }
    }

    @Test
    void testJdbcUrlValidatesServerCertificate() {
        assertThat(CONTAINER.getJdbcUrl(), containsString(";validateservercertificate=1;"));
    }

    @Test
    void testWithUsername() {
        final String expectedUsername = "JohnathanSmith";
        assertThat(CONTAINER.withUsername(expectedUsername).getUsername(), equalTo(expectedUsername));
    }

    @Test
    void testWithPassword() {
        final String expectedPwd = "open sesame!";
        assertThat(CONTAINER.withPassword(expectedPwd).getPassword(), equalTo(expectedPwd));
    }

    @Test
    void testGetClusterConfiguration() throws InterruptedException, ExasolContainerException {
        final ClusterConfiguration configuration = CONTAINER.getClusterConfiguration();
        assertThat(configuration.getDefaultBucketReadPassword(), not(emptyOrNullString()));
    }

    // [itest->dsn~exasol-container-provides-a-jdbc-connection-for-username-and-password~1]
    @Test
    void testCreateConnectionForUser() throws SQLException {
        try (final Connection connection = CONTAINER.createConnectionForUser(
                ExasolContainerConstants.DEFAULT_ADMIN_USER, ExasolContainerConstants.DEFAULT_SYS_USER_PASSWORD)) {
            assertThat("Connection established.", connection.isValid(5), equalTo(true));
        }
    }

    @Test
    void testGetLivenessCheckPortNumbers() {
        final Set<Integer> expectedPorts = Set.of(CONTAINER.getMappedPort(CONTAINER.getDefaultInternalDatabasePort()));
        assertThat(CONTAINER.getLivenessCheckPortNumbers(), equalTo(expectedPorts));
    }

    @Test
    void testGetExaConnectionAddress() {
        final String address = CONTAINER.getExaConnectionAddress();
        assertThat(address, matchesPattern("(?:[.0-9]+|localhost):[0-9]{1,5}"));
    }

    @Test
    void testCopyToContainerWorks() throws IOException, InterruptedException {
        final String containerPath = "/tmp/test.txt";
        final String content = "content";
        CONTAINER.copyFileToContainer(Transferable.of(content), containerPath);
        final ExecResult result = CONTAINER.execInContainer("cat", containerPath);
        assertAll(() -> assertThat(result.getExitCode(), is(0)),
                () -> assertThat(result.getStdout(), equalTo(content)));
    }
}
