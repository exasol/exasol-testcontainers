package com.exasol.clusterlogs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Tag("slow")
@Testcontainers
class MappedClusterLogsIT {
    private static final Path TEMP_DIR = createTempDir();

    private static Path createTempDir() {
        final Path tempDir = createTempPath();
        tempDir.toFile().deleteOnExit();
        return tempDir;
    }

    private static Path createTempPath() {
        try {
            return Files.createTempDirectory("MappedClusterLogsIT");
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>("8.23.0") //
            .withClusterLogsPath(TEMP_DIR) //
            .withRequiredServices();

    @Test
    // [itest->dsn~mapping-the-log-directory-to-the-host~1]
    void testMapClusterLogs() throws InterruptedException, IOException {
        final File syslogFile = TEMP_DIR.resolve("syslog").toFile();
        assertThat("File " + syslogFile + " exists", syslogFile.exists(), equalTo(true));
    }
}
