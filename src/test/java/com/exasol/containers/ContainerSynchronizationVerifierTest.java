package com.exasol.containers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.ContainerLaunchException;

@ExtendWith(MockitoExtension.class)
class ContainerSynchronizationVerifierTest {
    @Mock
    ContainerTimeService timeServiceMock;

    // [itest->req~clock-synchronization~1]
    @Test
    void testDetectSynchronizedClocks() throws ExasolContainerException {
        Mockito.when(this.timeServiceMock.getTime()).thenReturn(Instant.now());
        final ContainerSynchronizationVerifier verifier = ContainerSynchronizationVerifier.create(this.timeServiceMock);
        assertDoesNotThrow(() -> verifier.verifyClocksInSync());
    }

    // [itest->req~clock-synchronization~1]
    @Test
    void testDetectUnsynchronizedClocks() throws ExasolContainerException {
        Mockito.when(this.timeServiceMock.getTime()).thenReturn(Instant.EPOCH);
        final ContainerSynchronizationVerifier verifier = ContainerSynchronizationVerifier.create(this.timeServiceMock);
        assertThrows(ContainerLaunchException.class, () -> verifier.verifyClocksInSync());
    }
}