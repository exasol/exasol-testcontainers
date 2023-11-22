package com.exasol.drivers;

import static com.exasol.containers.ExasolService.BUCKETFS;
import static com.exasol.drivers.ExasolDriverManager.DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.UUID;

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
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>() //
            .withRequiredServices(BUCKETFS) //
            .withReuse(true);

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
        try (LocalDerbyServer derby = LocalDerbyServer.start(derbyTempDir, hostIP)) {
            final Driver derbyDriver = derby.getDriver();
            final Path driverPath = getPathToDriverJarFile(derbyDriver);
            final ExasolDriverManager exasolDriverManager = EXASOL.getDriverManager();
            final DatabaseDriver driver = JdbcDriver.builder(derby.getDriverName()) //
                    .prefix("jdbc:derby:") //
                    .sourceFile(driverPath) //
                    .mainClass(derbyDriver.getClass().getCanonicalName()) //
                    .enableSecurityManager(false) //
                    .build();
            exasolDriverManager.install(driver);
            final String expectedContent = "from Derby";
            prepareDerbyInMemoryTableWithContent(derby, expectedContent);
            try (final Connection exasolConnection = EXASOL.createConnection();
                    final Statement statement = exasolConnection.createStatement();
                    final ResultSet result = statement.executeQuery("IMPORT FROM JDBC " //
                            + "AT 'jdbc:derby://" + hostIP + ":" + LocalDerbyServer.DERBY_PORT + "/test' " //
                            + "USER '" + derby.getUsername() + "' IDENTIFIED BY '" + derby.getPassword() + "' " //
                            + "STATEMENT 'SELECT * FROM T'")) {
                result.next();
                assertThat(result.getString(1), equalTo(expectedContent));
            }
        }
    }

    private static Path getPathToDriverJarFile(final Driver driver) {
        return Path.of(driver.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    private void prepareDerbyInMemoryTableWithContent(final LocalDerbyServer derby, final String content)
            throws SQLException {
        try (final Connection derbyConnection = derby.getConnection();
                final Statement statement = derbyConnection.createStatement()) {
            statement.execute("CREATE TABLE T(C VARCHAR(40))");
            statement.execute("INSERT INTO T VALUES ('" + content + "')");
        }
    }
}
