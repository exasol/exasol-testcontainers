package com.exasol.clusterlogs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Tag("slow")
@Testcontainers
class MappedClusterLogsIT {

    @TempDir
    private static Path tempDir;

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>("8.23.0") //
            .withClusterLogsPath(tempDir) //
            .withRequiredServices();

    @Test
    // [itest->dsn~mapping-the-log-directory-to-the-host~1]
    void testMapClusterLogs() throws InterruptedException, IOException {
        final File syslogFile = tempDir.resolve("syslog").toFile();
        assertThat("File " + syslogFile + " exists", syslogFile.exists(), equalTo(true));
    }
}
