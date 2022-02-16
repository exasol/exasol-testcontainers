package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS;

import org.testcontainers.containers.ContainerLaunchException;

import com.exasol.errorreporting.ExaError;

/**
 * Check the synchronization of a given container in relation to the host running the Test Container software.
 */
public class ContainerSynchronizationVerifier {
    private final ContainerTimeService timeService;

    /**
     * Create a new {@link ContainerSynchronizationVerifier}.
     *
     * @param timeService service that provide the time in the container
     * @return new {@link ContainerSynchronizationVerifier} instance
     */
    public static ContainerSynchronizationVerifier create(final ContainerTimeService timeService) {
        return new ContainerSynchronizationVerifier(timeService);
    }

    /**
     * Create a synchronization verifier for a given container.
     *
     * @param timeService service that provides the container time
     */
    private ContainerSynchronizationVerifier(final ContainerTimeService timeService) {
        this.timeService = timeService;
    }

    /**
     * Verify that the clocks of container and host are synchronized.
     *
     * @throws ExasolContainerException if the clocks are not synchronized.
     */
    // [impl->dsn~clock-synchronization~1]
    public void verifyClocksInSync() throws ExasolContainerException {
        final long offset = System.currentTimeMillis() - this.timeService.getMillisSinceEpochUtc();
        if (Math.abs(offset) > MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS) {
            throw new ContainerLaunchException(ExaError.messageBuilder("E-ETC-17") //
                    .message("The clock of the Exasol VM is offset by up to {{offset}} ms in relation to the clock of "
                            + " the host running test containers. Note that the measured offset has a limited "
                            + " precision caused by the latency of querying the time in the container, so the actual"
                            + " offset might be a couple of milliseconds better than reported here."
                            + " The maximum allowed offset in any direction is {{maximum-offset}} ms.") //
                    .parameter("offset", offset, "actual clock offset in milliseconds") //
                    .parameter("maximum-offset", MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS,
                            "maximum allowed offest in milliseconds") //
                    .mitigation("Use a time synchronization tool like NTP to ensure sychronized clocks.").toString());
        }
    }
}