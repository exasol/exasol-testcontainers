package com.exasol.containers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("slow")
@Testcontainers
class ContainerSynchronizationVerifierIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>();

    // [itest->req~clock-synchronization~1]
    @Test
    void testDetectUnsynchronizedClocks() throws ExasolContainerException {
        final ContainerSynchronizationVerifier verifier = ContainerSynchronizationVerifier.create(CONTAINER);
        try {
            CONTAINER.execInContainer("date", "-s01/01/2000 00:00:00");
            assertThrows(ContainerLaunchException.class, () -> verifier.verifyClocksInSync());
        } catch (UnsupportedOperationException | IOException exception) {
            fail("Unable to create intentional offset for sync test.");
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("Introduction of offset in sync test got interrupted.");
        }
    }
}