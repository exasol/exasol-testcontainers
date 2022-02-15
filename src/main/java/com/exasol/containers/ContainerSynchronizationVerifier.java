package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS;

import java.io.IOException;

import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.ContainerLaunchException;

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
    // [impl->req~clock-synchronization~1]
    public void verifyClocksInSync() throws ExasolContainerException {
        final double offset = getClockOffestInMilliseconds();
        if (Math.abs(offset) > MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS) {
            throw new ContainerLaunchException(ExaError.messageBuilder("E-ETC-17") //
                    .message("The clock of the Exasol VM is offset by up to{{offset}} ms in relation to the clock of "
                            + " the host running test containers. Note that there the measured offset has a limited "
                            + " precision caused by the latency of querying the time in the container, so the actual"
                            + " offset might be a couple of milliseconds better than reported here."
                            + " The maximum allowed offset in any direction is {{maximum-offset}}.") //
                    .parameter("offset", offset) //
                    .parameter("maximum-offset", MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS) //
                    .mitigation("Use a time synchronization tool like NTP to ensure sychronized clocks.").toString());
        }
    }

    /**
     * Determine the offset between container clock an host clock.
     *
     * @return offset between container clock and host clock in milliseconds
     * @throws ExasolContainerException if the time in the container can't be determined.
     */
    private double getClockOffestInMilliseconds() throws ExasolContainerException {
        try {
            final ExecResult containerSecondsSinceEpoch = this.container.execInContainer("date", "+%s.%N");
            final double containerMillis = Double.parseDouble(containerSecondsSinceEpoch.getStdout()) * 1000;
            final double hostMillis = System.currentTimeMillis();
            return containerMillis - hostMillis;
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