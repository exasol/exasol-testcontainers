package com.exasol.containers.wait.strategy;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;

import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;

/**
 * Strategy that waits for a container to be ready by checking when the language container is unpacked completely.
 */
public class LogFileEntryWaitStrategy extends AbstractWaitStrategy {
    private static final long WAIT_DURATION_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(2);
    private static final long POLLING_DELAY_IN_MILLISECONDS = TimeUnit.SECONDS.toMillis(1);
    private final LogPatternDetector detector;

    /**
     * Create a new instance of the {@link LogFileEntryWaitStrategy} where the log message is expected to appear after
     * the time specified in the parameter {@code afterUTc}.
     *
     * @param detectorFactory log entry pattern detector factory
     * @param logPath         path of the log file to search
     * @param logNamePattern  pattern used to find the file name
     * @param pattern         regular expression pattern for which to look out
     * @param afterUtc        earliest time in the log after which the log message must appear
     */
    public LogFileEntryWaitStrategy(final LogPatternDetectorFactory detectorFactory, final String logPath,
            final String logNamePattern, final String pattern, final Instant afterUtc) {
        this(detectorFactory.createLogPatternDetector(logPath, logNamePattern, pattern, afterUtc));
    }

    /**
     * Create a new instance of the {@link LogFileEntryWaitStrategy} where the log message is expected to appear after
     * the time specified in the parameter {@code afterUTc}.
     *
     * @param detector log entry pattern detector
     */
    public LogFileEntryWaitStrategy(final LogPatternDetector detector) {
        this.detector = detector;
    }

    @Override
    protected void waitUntilReady() {
        final long expiry = System.currentTimeMillis() + getWaitTimeOutMilliseconds();
        while (System.currentTimeMillis() < expiry) {
            try {
                if (this.detector.isPatternPresent()) {
                    return;
                }
                Thread.sleep(POLLING_DELAY_IN_MILLISECONDS);
            } catch (final UnsupportedOperationException | IOException exception) {
                throw new ContainerLaunchException("Caught exception while waiting for log entry.", exception);
            } catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new ContainerLaunchException("Waiting for log entry got interrupted.", exception);
            }
        }
        throw new ContainerLaunchException("Timeout: " + this.detector.describe() + "\nActual log file content: \""
                + this.detector.getActualLog() + "\"");
    }

    /**
     * Get the timeout in milliseconds.
     *
     * @return timeout in milliseconds
     */
    protected long getWaitTimeOutMilliseconds() {
        return WAIT_DURATION_IN_MILLISECONDS;
    }
}