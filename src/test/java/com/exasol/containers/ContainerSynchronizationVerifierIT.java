package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("slow")
@Testcontainers
class ContainerSynchronizationVerifierIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>();

    @Test
    void testDetectUnsynchronizedClocks() throws ExasolContainerException {
        final ContainerSynchronizationVerifier verifier = ContainerSynchronizationVerifier.create(CONTAINER);
        assertThat(verifier.isClockInSync(), equalTo(true));
        try {
            CONTAINER.execInContainer("date", "-s01/01/2000 00:00:00");
            assertThat(verifier.isClockInSync(), equalTo(false));
        } catch (UnsupportedOperationException | IOException exception) {
            fail("Unable to create intentional offset for sync test.");
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("Introduction of offset in sync test got interrupted.");
        }
    }
}