package com.exasol.containers.wait.strategy;

import java.io.IOException;
import java.time.Instant;

import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;

import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;

/**
 * Strategy that waits for a container to be ready by checking when the language container is unpacked completely.
 */
public class LogFileEntryWaitStrategy extends AbstractWaitStrategy {
    private static final long WAIT_DURATION_IN_MILLISECONDS = 60000;
    private static final long POLLING_DELAY_IN_MILLISECONDS = 1000;
    private final LogPatternDetector detector;
    private final Instant afterUTC;

    /**
     * Create a new instance of the {@link LogFileEntryWaitStrategy}
     *
     * @param detectorFactory log entry pattern detector factory
     * @param logPath         path of the log file to search
     * @param logNamePattern  pattern used to find the file name
     * @param pattern         regular expression pattern for which to look out
     */
    public LogFileEntryWaitStrategy(final LogPatternDetectorFactory detectorFactory, final String logPath,
            final String logNamePattern, final String pattern) {
        super();
        this.afterUTC = Instant.now();
        this.detector = detectorFactory.createLogPatternDetector(logPath, logNamePattern, pattern);
    }

    @Override
    protected void waitUntilReady() {
        final long expiry = System.currentTimeMillis() + (WAIT_DURATION_IN_MILLISECONDS);
        while (System.currentTimeMillis() < expiry) {
            try {
                if (this.detector.isPatternPresentAfter(this.afterUTC)) {
                    return;
                }
                Thread.sleep(POLLING_DELAY_IN_MILLISECONDS);
            } catch (UnsupportedOperationException | IOException exception) {
                throw new ContainerLaunchException("Caught exception while waiting for log entry.", exception);
            } catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new ContainerLaunchException("Waiting for log entry got interrupted.", exception);
            }
        }
        throw new ContainerLaunchException("Timeout: " + this.detector.describe());
    }
}