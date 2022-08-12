package com.exasol.clusterlogs;

import java.time.Instant;
import java.util.TimeZone;

import org.testcontainers.containers.Container;

import com.exasol.containers.ExasolContainer;

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
    public LogPatternDetector createLogPatternDetector(final String logPath, final String logNamePattern,
            final String pattern, final Instant afterUtc) {
        TimeZone timeZone = this.container.getClusterConfiguration().getTimeZone();
        timeZone = TimeZone.getTimeZone("UTC"); // TODO: Fix me!
        final TimestampLogEntryPatternVerifier logEntryVerifier = //
                new TimestampLogEntryPatternVerifier(afterUtc, timeZone);
        return LogPatternDetector.builder() //
                .container(this.container) //
                .logPath(logPath) //
                .logNamePattern(logNamePattern) //
                .pattern(pattern) //
                .logEntryVerifier(logEntryVerifier) //
                .build();
    }

    public LogPatternDetector lineCountingDetector(final String logPath, final String logNamePattern,
            final String pattern, final long afterLineNumber) {
        return LogPatternDetector.builder() //
                .container(this.container) //
                .logPath(logPath) //
                .logNamePattern(logNamePattern) //
                .pattern(pattern) //
                .afterLine(afterLineNumber) //
                .build();
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
}