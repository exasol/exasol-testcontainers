package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.config.ClusterConfiguration;
import com.exasol.containers.ssh.DockerAccess;
import com.jcraft.jsch.JSchException;

// [itest->dsn~exasol-container-controls-docker-container~1]
// [itest->dsn~exasol-container-ready-criteria~3]

// This test contains test cases that modify the configuration of the container. Don't add test
// cases that depend on the default settings!
@Tag("slow")
@Testcontainers
class ExasolContainerIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainerIT.class);

    @Container // [itest->dsn~exasol-container-starts-with-test~1]
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withReuse(true).withRequiredServices(ExasolService.JDBC);

    // [itest->dsn~exasol-container-uses-privileged-mode~1]
    @Test
    void testContainerRunsInPrivilegedMode() {
        assertThat(CONTAINER.isPrivilegedMode(), equalTo(true));
    }

    @Test
    void testSsh() throws JSchException, IOException, UnsupportedOperationException, InterruptedException {
        final DockerAccess accessProvider = CONTAINER.getAccessProvider();
        System.out.println("Container " //
                + (accessProvider.supportsDockerExec() ? "supports" : "does not support") //
                + " docker exec.");
        LOGGER.info("container date: {}", ContainerTimeService.create(CONTAINER).getTime());

        final ClusterConfiguration config = CONTAINER.getClusterConfiguration();
        LOGGER.info("db version {}, time zone {}", config.getDBVersion(), config.getTimeZone().getID());

        final String remotePath = "/root/a-new-file.txt";
        CONTAINER.copyFileToContainer(Paths.get("c:/HOME/Tmp/a.txt"), remotePath);
        final String content = accessProvider.getSsh().readRemoteFile(remotePath);
        System.out.println(content);
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
                "jdbc:exa:" + hostNamePattern + ":\\d{1,5};validateservercertificate=1;fingerprint=\\w{64};"));
    }

    @Test
    void testJdbcUrlValidatesServerCertificate() throws SQLException {
        assertThat(CONTAINER.getJdbcUrl(), containsString(";validateservercertificate=1"));
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
}