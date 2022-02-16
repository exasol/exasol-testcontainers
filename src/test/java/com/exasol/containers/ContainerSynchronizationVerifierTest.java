package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    // [itest->dsn~clock-synchronization~1]
    @Test
    void testDetectSynchronizedClocks() throws ExasolContainerException {
        Mockito.when(this.timeServiceMock.getMillisSinceEpochUtc()).thenReturn(System.currentTimeMillis());
        final ContainerSynchronizationVerifier verifier = ContainerSynchronizationVerifier.create(this.timeServiceMock);
        assertDoesNotThrow(() -> verifier.verifyClocksInSync());
    }

    // [itest->dsn~clock-synchronization~1]
    @Test
    void testDetectUnsynchronizedClocks() throws ExasolContainerException {
        final long timeTooLongBehind = System.currentTimeMillis() - MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS - 1;
        Mockito.when(this.timeServiceMock.getMillisSinceEpochUtc()).thenReturn(timeTooLongBehind);
        final ContainerSynchronizationVerifier verifier = ContainerSynchronizationVerifier.create(this.timeServiceMock);
        final Throwable exception = assertThrows(ContainerLaunchException.class, () -> verifier.verifyClocksInSync());
        assertThat(exception.getMessage(), containsString("The clock of the Exasol VM is offset"));
    }
}