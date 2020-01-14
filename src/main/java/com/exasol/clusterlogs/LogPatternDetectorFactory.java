package com.exasol.clusterlogs;

import org.testcontainers.containers.Container;

/**
 * Factory for log entry scanners.
 */
public class LogPatternDetectorFactory {
    private final Container<? extends Container<?>> container;

    /**
     * Create a new instance of a {@link LogPatternDetectorFactory}.
     *
     * @param container container used to execute the underlying detection commands
     */
    public LogPatternDetectorFactory(final Container<? extends Container<?>> container) {
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
        return new LogPatternDetector(this.container, logPath, logNamePattern, pattern);
    }
}