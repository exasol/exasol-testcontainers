package com.exasol.containers.wait;

import java.io.IOException;

import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;

/**
 * Strategy that waits for a container to be ready by checking when the language container is unpacked completely.
 */
public class LogFileEntryWaitStrategy extends AbstractWaitStrategy {
    private static final int WAIT_DURATION_IN_MILLISECONDS = 60000;
    private static final int POLLING_RATE_LIMIT_IN_MILLISECONDS = 1000;
    private final Container<? extends Container<?>> container;
    private final String logPath;
    private final String pattern;
    private final String logNamePattern;

    /**
     * Create a new instance of the {@link LogFileEntryWaitStrategy}
     *
     * @param container      container in which the log messages reside
     * @param logPath        path of the log file to search
     * @param logNamePattern pattern used to find the file name
     * @param pattern        regular expression pattern for which to look out
     */
    public LogFileEntryWaitStrategy(final Container<? extends Container<?>> container, final String logPath,
            final String logNamePattern, final String pattern) {
        super();
        this.container = container;
        this.logPath = logPath;
        this.logNamePattern = logNamePattern;
        this.pattern = pattern;
    }

    @Override
    protected void waitUntilReady() {
        final long expiry = System.currentTimeMillis() + (WAIT_DURATION_IN_MILLISECONDS);
        while (System.currentTimeMillis() < expiry) {
            try {
                final ExecResult result = this.container.execInContainer("find", this.logPath, "-name",
                        this.logNamePattern, "-exec", "grep", this.pattern, "{}", "+");
                if (result.getExitCode() == 0) {
                    return;
                }
                Thread.sleep(POLLING_RATE_LIMIT_IN_MILLISECONDS);
            } catch (UnsupportedOperationException | IOException exception) {
                throw new ContainerLaunchException("Caught exception while waiting for log entry.", exception);
            } catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new ContainerLaunchException("Waiting for log entry got interrupted.", exception);
            }
        }
        throw new ContainerLaunchException("Time out waiting for log message \"" + this.pattern + " to appear in \""
                + this.logPath + "/" + this.logNamePattern + "\".");
    }
}