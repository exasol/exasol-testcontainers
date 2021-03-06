package com.exasol.clusterlogs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.io.Files;

import com.exasol.containers.ExasolContainer;

@Tag("slow")
@Testcontainers
class MappedClusterLogsIT {
    private static final Path TEMP_DIR = createTempDir();

    private static Path createTempDir() {
        final File tempDir = Files.createTempDir();
        tempDir.deleteOnExit();
        return tempDir.toPath();
    }

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>() //
            .withClusterLogsPath(TEMP_DIR) //
            .withRequiredServices();

    @Test
    // [itest->dsn~mapping-the-log-directory-to-the-host~1]
    void testMapClusterLogs() throws InterruptedException, IOException {
        assertThat(TEMP_DIR.resolve("syslog").toFile().exists(), equalTo(true));
    }
}