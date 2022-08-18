package com.exasol.clusterlogs;

import org.testcontainers.containers.Container;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;
import com.exasol.bucketfs.monitor.LineCountRetriever;
import com.exasol.bucketfs.testcontainers.LogPatternProvider;
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
     * @param state          state to allow filtering of log file entries
     * @return detector instance
     */
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

    /**
     * @param logPath        path in which to look for logs
     * @param logNamePattern pattern for log names
     * @return {@link LineCountRetriever} able to count the number of lines of the log file with given {@code logPath}
     *         and {@code logNamePattern}
     */
    public LineCountRetriever createLineCountRetriever(final String logPath, final String logNamePattern) {
        return new LineCountRetriever(this.container, logPath, logNamePattern);
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

    /**
     * @return {@link LogPatternProvider} depending on major version of current docker image
     */
    public LogPatternProvider getLogPatternProvider() {
        final ExasolDockerImageReference image = this.container.getDockerImageReference();
        return (image.hasMajor() && (image.getMajor() < 8)) //
                ? LogPatternProvider.DEFAULT
                : LogPatternProvider.VERSION_8;
    }
}