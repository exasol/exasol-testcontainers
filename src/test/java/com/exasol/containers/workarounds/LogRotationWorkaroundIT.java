package com.exasol.containers.workarounds;

import static com.exasol.containers.DockerImageReferenceFactory.versionFromSystemPropertyOrIndividual;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.exec.ExitCode;
import com.exasol.containers.ssh.SshException;

@Testcontainers
@Tag("expensive")
class LogRotationWorkaroundIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogRotationWorkaround.class);
    private static final int LS_SIZE_COLUMN_NUMBER = 4;
    private static final int MILLIS_PER_SECOND = 1000;

    // [itest->dsn~log-rotation-workaround~1]
    @SuppressWarnings("java:S2925") // sleep is necessary for polling here.
    @Test
    void testLogRotationWorkaround() {
        final String version = versionFromSystemPropertyOrIndividual("7.0.4");
        try (final ExasolContainer<? extends ExasolContainer<?>> exasol = new ExasolContainer<>(version)) {
            exasol.start();
            for (int round = 0; round < 60; ++round) {
                assertLogRotationConfigurationContent(exasol, round);
                assertBucketFsLogNotEmpty(exasol, round);
                Thread.sleep(60 * LogRotationWorkaroundIT.MILLIS_PER_SECOND);
            }
            if (exasol.isRunning()) {
                exasol.stop();
            }
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted during sleep between check rounds.");
        }
    }

    private void assertLogRotationConfigurationContent(final ExasolContainer<? extends ExasolContainer<?>> exasol,
            final int round) {
        try {
            final ExecResult result = exasol.execInContainer("grep", "bucketfsd", "/etc/cron.daily/exa-logrotate");
            if (result.getExitCode() == ExitCode.OK) {
                throw new AssertionError(
                        "Found \"bucketfs\" in log rotation configuration that should not be there in round " + round
                                + ":\n" + result.getStdout());
            }
        } catch (final UnsupportedOperationException | SshException | IOException exception) {
            throw new AssertionError("Unable to check whether log rotation configuration is correct.", exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted during check whether log rotation configuration is correct.");
        }
    }

    private void assertBucketFsLogNotEmpty(final ExasolContainer<? extends ExasolContainer<?>> exasol,
            final int round) {
        try {
            final ExecResult result = exasol.execInContainer("ls", "-l", "/exa/logs/cored");
            if (result.getExitCode() == ExitCode.OK) {
                final String listing = result.getStdout().trim();
                boolean found = false;
                for (final String line : listing.split("\\n")) {
                    if (line.contains("bucketfs")) {
                        LogRotationWorkaroundIT.LOGGER.info("Found BucketFS log entry: " + line);
                        found = true;
                        final String[] columns = line.split("\\s+");
                        if (Integer.parseInt(columns[LS_SIZE_COLUMN_NUMBER]) == 0) {
                            throw new AssertionError("BucketFS log file is empty in round " + round + ".");
                        }
                    }
                }
                if (!found) {
                    throw new AssertionError("BucketFS log file is missing in round " + round + ".");
                }
            } else {
                throw new AssertionError("Unable to list contents of log directory trying to check BucketFS log.");
            }
        } catch (final UnsupportedOperationException | IOException exception) {
            throw new AssertionError("Unable to check BucketFS log file.", exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted during check of BucketFS log file.");
        }
    }
}