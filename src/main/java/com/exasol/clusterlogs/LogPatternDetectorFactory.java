package com.exasol.clusterlogs;

import java.time.Instant;
import java.util.TimeZone;

import org.testcontainers.containers.Container;

import com.exasol.bucketfs.monitor.FileSizeRetriever;
import com.exasol.bucketfs.monitor.StateBasedBucketFsMonitor.State;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolDockerImageReference;

/**
 * Factory for log entry scanners.
 */
public class LogPatternDetectorFactory {
    private final ExasolContainer<? extends Container<?>> container;

    /**
     * Create a new instance of a {@link LogPatternDetectorFactory}.
     *
     * @param container container used to execute the underlying detection commands
     */
    public LogPatternDetectorFactory(final ExasolContainer<? extends ExasolContainer<?>> container) {
        this.container = container;
    }

    /**
     * Create a new {@link LogPatternDetector} that verifies that log entries appear after the given timestamp.
     *
     * @param logPath        path in which to look for logs
     * @param logNamePattern pattern for log names
     * @param pattern        pattern for which to search inside logs
     * @param afterUtc       earliest time in the log after which the log message must appear
     * @return detector instance
     */
    @Deprecated
    public LogPatternDetector createLogPatternDetector(final String logPath, final String logNamePattern,
            final String pattern, final Instant afterUtc) {
        TimeZone timeZone = this.container.getClusterConfiguration().getTimeZone();
        timeZone = TimeZone.getTimeZone("UTC"); // TODO: Fix me!
        final TimestampLogEntryPatternVerifierOld logEntryVerifier = //
                new TimestampLogEntryPatternVerifierOld(afterUtc, timeZone);
        return LogPatternDetector.builder() //
                .container(this.container) //
                .logPath(logPath) //
                .logNamePattern(logNamePattern) //
                .pattern(pattern) //
                .logEntryVerifier(logEntryVerifier) //
                .build();
    }

    public LogPatternDetector createLogPatternDetector(final String logPath, final String logNamePattern,
            final String pattern, final State state) {
        return LogPatternDetector.builder() //
                .container(this.container) //
                .logPath(logPath) //
                .logNamePattern(logNamePattern) //
                .pattern(pattern) //
                .forState(state) //
                .build();
    }

    public FileSizeRetriever createFileSizeRetriever(final String logPath, final String logNamePattern) {
        return new FileSizeRetriever(this.container, logPath, logNamePattern);
    }

    /**
     * Create a new {@link LogPatternDetector} that ignores log entry timestamps.
     *
     * @param logPath        path in which to look for logs
     * @param logNamePattern pattern for log names
     * @param pattern        pattern for which to search inside logs
     * @return detector instance
     */
    public LogPatternDetector createLogPatternDetector(final String logPath, final String logNamePattern,
            final String pattern) {
        return LogPatternDetector.builder() //
                .container(this.container) //
                .logPath(logPath) //
                .logNamePattern(logNamePattern) //
                .pattern(pattern) //
                .logEntryVerifier(new LogEntryPresentPatternVerifier()) //
                .build();
    }

    public ExasolDockerImageReference getDockerImageReference() {
        return this.container.getDockerImageReference();
    }
}