package com.exasol.drivers;

import static com.exasol.containers.ExasolService.BUCKETFS;
import static com.exasol.drivers.ExasolDriverManager.DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.io.Files;

import com.exasol.bucketfs.*;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolContainerAssumptions;

@Tag("slow")
@Testcontainers
class ExasolDriverManagerIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>() //
            .withRequiredServices(BUCKETFS) //
            .withReuse(true);

    // [itest->dsn~installing-a-jdbc-driver-from-host-filesystem~1]
    @Test
    void testInstallDriver(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, InterruptedException {
        ExasolContainerAssumptions.assumeDockerDbVersionNotOverriddenToBelowExasolSeven();
        final String expectedDriverContent = "expected driver content";
        final String fileName = "dummy_driver_" + UUID.randomUUID() + ".jar";
        final Path driverFile = tempDir.resolve(fileName);
        Files.write(expectedDriverContent.getBytes(), driverFile.toFile());
        final ExasolDriverManager driverManager = EXASOL.getDriverManager();
        final DatabaseDriver driver = JdbcDriver.builder("DUMMY_DRIVER") //
                .prefix("jdbc:dummy:") //
                .sourceFile(driverFile) //
                .mainClass("org.example.DummyDriver").build();
        driverManager.install(driver);
        final Bucket bucket = EXASOL.getDefaultBucket();
        final List<String> list = bucket
                .listContents(DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET + BucketConstants.PATH_SEPARATOR);
        assertAll(() -> assertThat("Driver file in Bucket", list, hasItem(fileName)),
                () -> assertThat("Manifest file in Bucket", list, hasItem("settings.cfg")));
    }
}