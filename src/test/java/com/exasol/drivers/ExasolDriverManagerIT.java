package com.exasol.drivers;

import static com.exasol.containers.ExasolService.BUCKETFS;
import static com.exasol.drivers.ExasolDriverManager.DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.UUID;

import org.apache.derby.drda.NetworkServerControl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.bucketfs.*;
import com.exasol.containers.ExasolContainer;

@Tag("slow")
@Testcontainers
class ExasolDriverManagerIT {
    private static final String DERBY_DRIVER_NAME = "DERBY";
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>() //
            .withRequiredServices(BUCKETFS) //
            .withReuse(true);
    public static final int DERBY_PORT = 1527;
    public static final String DERBY_USER = "app";
    public static final String DERBY_PASSWORD = "open_sesame";

    // [itest->dsn~installing-a-jdbc-driver-from-host-filesystem~1]
    @Test
    void testInstallDriver(@TempDir final Path tempDir) throws IOException, BucketAccessException {
        final String expectedDriverContent = "expected driver content";
        final String fileName = "dummy_driver_" + UUID.randomUUID() + ".jar";
        final Path driverFile = tempDir.resolve(fileName);
        Files.writeString(driverFile, expectedDriverContent);
        final ExasolDriverManager exasolDriverManager = EXASOL.getDriverManager();
        final DatabaseDriver driver = JdbcDriver.builder("DUMMY_DRIVER") //
                .prefix("jdbc:dummy:") //
                .sourceFile(driverFile) //
                .mainClass("org.example.DummyDriver") //
                .build();
        exasolDriverManager.install(driver);
        final Bucket bucket = EXASOL.getDefaultBucket();
        final List<String> list = bucket
                .listContents(DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET + BucketConstants.PATH_SEPARATOR);
        assertAll(() -> assertThat("Driver file in Bucket", list, hasItem(fileName)),
                () -> assertThat("Manifest file in Bucket", list, hasItem("settings.cfg")));
    }

    // [itest->dsn~installing-a-jdbc-driver-from-host-filesystem~1]
    @Test
    void testInstallDerbyDriver(@TempDir final Path derbyTempDir) throws Exception {
        final String hostIP = EXASOL.getHostIp();
        final NetworkServerControl derbyServer = startDerbyServer(derbyTempDir, hostIP);
        try {
            waitForDerbyServerToAcceptConnections(derbyServer);
            final Driver derbyDriver = DriverManager.getDriver("jdbc:derby://localhost");
            final Path driverPath = getPathToDriverJarFile(derbyDriver);
            final ExasolDriverManager exasolDriverManager = EXASOL.getDriverManager();
            final DatabaseDriver driver = JdbcDriver.builder(DERBY_DRIVER_NAME) //
                    .prefix("jdbc:derby:") //
                    .sourceFile(driverPath) //
                    .mainClass(derbyDriver.getClass().getCanonicalName()) //
                    .enableSecurityManager(false) //
                    .build();
            exasolDriverManager.install(driver);
            final String expectedContent = "from Derby";
            prepareDerbyInMemoryTableWithContent(expectedContent, hostIP);
            try (final Connection exasolConnection = EXASOL.createConnection();
                    final Statement statement = exasolConnection.createStatement();
                    final ResultSet result = statement.executeQuery("IMPORT FROM JDBC " //
                            + "AT 'jdbc:derby://" + hostIP + ":" + DERBY_PORT + "/test' " //
                            + "USER '" + DERBY_USER + "' IDENTIFIED BY '" + DERBY_PASSWORD + "' " //
                            + "STATEMENT 'SELECT * FROM T'")) {
                result.next();
                assertThat(result.getString(1), equalTo(expectedContent));
            }
        } finally {
            shutDownDerbyServer(derbyServer, hostIP);
        }
    }

    private static Path getPathToDriverJarFile(final Driver driver) {
        return Path.of(driver.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    private void prepareDerbyInMemoryTableWithContent(final String content, final String hostIP) throws SQLException {
        final String jdbcUrl = "jdbc:derby://" + hostIP + ":" + DERBY_PORT + "/test;create=true;";
        try (final Connection derbyConnection = DriverManager.getConnection(jdbcUrl);
                final Statement statement = derbyConnection.createStatement()) {
            statement.execute("CREATE TABLE T(C VARCHAR(40))");
            statement.execute("INSERT INTO T VALUES ('" + content + "')");
        }
    }

    private NetworkServerControl startDerbyServer(final Path derbyHomeDir, final String hostIP) throws Exception {
        System.setProperty("derby.system.home", derbyHomeDir.toString());
        System.setProperty("derby.user." + DERBY_USER, DERBY_PASSWORD);
        final NetworkServerControl derbyServer = new NetworkServerControl(InetAddress.getByName(hostIP), DERBY_PORT);
        derbyServer.start(new PrintWriter(System.out));
        return derbyServer;
    }

    @SuppressWarnings("java:S2925") // We need to wait for the server to come up with "sleep" in a loop.
    private static void waitForDerbyServerToAcceptConnections(final NetworkServerControl derbyServer)
            throws InterruptedException {
        for (int attempts = 0; attempts < 20; ++attempts) {
            try {
                derbyServer.ping();
                return;
            } catch (final Exception exception) {
                Thread.sleep(500);
            }
        }
        throw new IllegalStateException("Derby database server required for integration tests did not start up.");
    }

    private static void shutDownDerbyServer(final NetworkServerControl derbyServer, final String hostIp)
            throws Exception {
        if (derbyServer != null) {
            try {
                DriverManager.getConnection("jdbc:derby://" + hostIp + "/test;shutdown=true", DERBY_USER,
                        DERBY_PASSWORD);
            } catch (final SQLNonTransientConnectionException exception) {
                if (!exception.getMessage().contains("SQLSTATE: 08006")) {
                    throw new RuntimeException("Failed to shut down Derby database server.", exception);
                }
            }
        }
    }
}
