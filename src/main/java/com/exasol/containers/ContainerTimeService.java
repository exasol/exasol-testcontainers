package com.exasol.containers;

import java.io.IOException;
import java.time.Instant;

import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.exec.ExitCode;
import com.exasol.errorreporting.ExaError;

/**
 * This service abstracts the method of getting time information from the container.
 */
public class ContainerTimeService {
    private final Container<? extends Container<?>> container;

    /**
     * Create a new {@link ContainerTimeService}.
     *
     * @param container container from which to extract time information
     * @return new {@link ContainerTimeService} instance
     */
    public static ContainerTimeService create(final Container<? extends Container<?>> container) {
        return new ContainerTimeService(container);
    }

    /**
     * Create a new {@link ContainerTimeService} instance.
     *
     * @param container container from which to extract time information
     */
    private ContainerTimeService(final Container<? extends Container<?>> container) {
        this.container = container;
    }

    /**
     * Get the current time of the container.
     *
     * @return current container time
     */
    public Instant getTime() {
        return Instant.ofEpochMilli(getMillisSinceEpochUtc());
    }

    /**
     * Get the current time as milliseconds since Epoch UTC.
     *
     * @return number of milliseconds since Epoch UTC.
     */
    public long getMillisSinceEpochUtc() {
        try {
            final ExecResult result = this.container.execInContainer("date", "+%s%3N");
            if (result.getExitCode() == ExitCode.OK) {
                return Long.parseLong(result.getStdout().trim());
            } else {
                throw new ExasolContainerException(ExaError.messageBuilder("E-ETC-18") //
                        .message("Unable to get ISO time from container via 'date' command: {{error}}") //
                        .parameter("error", result.getStderr(), "Error output of command") //
                        .toString());
            }
        } catch (UnsupportedOperationException | IOException exception) {
            throw new ExasolContainerException(ExaError.messageBuilder("E-ETC-16")
                    .message("Unable to get current time from container trying to check synchronization with host.")
                    .toString(), exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ExasolContainerException(exception);
        }
    }
}