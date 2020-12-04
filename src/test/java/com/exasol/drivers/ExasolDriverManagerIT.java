package com.exasol.drivers;

import static com.exasol.containers.ExasolService.BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.io.Files;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketConstants;
import com.exasol.containers.ExasolContainer;

@Testcontainers
class ExasolDriverManagerIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>() //
            .withRequiredServices(BUCKETFS) //
            .withReuse(true);

    // [itest->dsn~installing-a-jdbc-driver-from-host-filesystem~1]
    @Test
    void testInstallDriver(@TempDir final Path tempDir) throws IOException {
        final Instant beforeInstallation = Instant.now();
        final String expectedDriverContent = "expected driver content";
        final String fileName = "dummy_driver.jar";
        final Path driverFile = tempDir.resolve(fileName);
        Files.write(expectedDriverContent.getBytes(), driverFile.toFile());
        final ExasolDriverManager driverManager = EXASOL.getDriverManager();
        final DatabaseDriver driver = JdbcDriver.builder("DUMMY_DRIVER") //
                .prefix("jdbc:dummy:") //
                .sourceFile(driverFile) //
                .mainClass("org.example.DummyDriver").build();
        driverManager.install(driver);
        final Bucket bucket = EXASOL.getDefaultBucket();
        assertAll(
                () -> assertThat("Driver file in Bucket",
                        bucket.isObjectSynchronized(ExasolDriverManager.DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET
                                + BucketConstants.PATH_SEPARATOR + fileName, beforeInstallation),
                        equalTo(true)),
                () -> assertThat("Manifest file in Bucket", bucket.isObjectSynchronized(
                        ExasolDriverManager.DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET, beforeInstallation), equalTo(true)));
    }
}