package com.exasol.clusterlogs;

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
     * Create a new {@link LogPatternDetector}.
     *
     * @param logPath        path in which to look for logs
     * @param logNamePattern pattern for log names
     * @param pattern        pattern for which to search inside logs
     * @return detector instance
     */
    public LogPatternDetector createLogPatternDetector(final String logPath, final String logNamePattern,
            final String pattern) {
        final TimeZone timeZone = this.container.getClusterConfiguration().getTimeZone();
        return new LogPatternDetector(this.container, logPath, logNamePattern, pattern, timeZone);
    }
}