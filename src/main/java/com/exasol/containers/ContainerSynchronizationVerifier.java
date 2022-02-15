package com.exasol.containers;

import java.io.IOException;

import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.errorreporting.ExaError;

/**
 * Check the synchronization of a given container in relation to the host running the Test Container software.
 */
public class ContainerSynchronizationVerifier {
    private final Container<? extends Container<?>> container;

    /**
     * Factory method for a {@link ContainerSynchronizationVerifier}.
     *
     * @param container which's synchronization is checked against the host.
     * @return new {@link ContainerSynchronizationVerifier} instance
     */
    public static ContainerSynchronizationVerifier create(final Container<? extends Container<?>> container) {
        return new ContainerSynchronizationVerifier(container);
    }

    /**
     * Create a synchronization verifier for a given container.
     *
     * @param container container which's synchronization is checked in relation to the host.
     */
    private ContainerSynchronizationVerifier(final Container<? extends Container<?>> container) {
        this.container = container;
    }

    /**
     * Verify that the clocks of container and host are synchronized.
     *
     * @throws ExasolContainerException if the clocks are not synchronized.
     */
    public void verifyClocksInSync() throws ExasolContainerException {
        if (!isClockInSync()) {
            throw new IllegalStateException(ExaError.messageBuilder("E-ETC-7")
                    .message("The clock of the Exasol VM and the host running test containers are not in snyc.")
                    .mitigation("Use a time synchronization tool like NTP to ensure sychronized clocks.").toString());
        }
    }

    /**
     * Check if the clocks of the machine running the test container and the Docker are synchronized.
     * <p>
     * Synchronized means that the times are have a maximum difference of 10 milliseconds.
     * </p>
     *
     * @return {@code true} if the clocks are synchronized
     * @throws ExasolContainerException if the time in the container can't be determined.
     */
    public boolean isClockInSync() throws ExasolContainerException {
        try {
            final ExecResult containerSecondsSinceEpoch = this.container.execInContainer("date", "+%s.%N");
            final double containerMillis = Double.parseDouble(containerSecondsSinceEpoch.getStdout()) * 1000;
            final double hostMillis = System.currentTimeMillis();
            return Math.abs(containerMillis - hostMillis) <= 10;
        } catch (UnsupportedOperationException | IOException exception) {
            throw new ExasolContainerException(ExaError.messageBuilder("E-ETC-6")
                    .message("Unable to get current time from container trying to check synchronization with host.")
                    .toString(), exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ExasolContainerException(exception);
        }
    }
}