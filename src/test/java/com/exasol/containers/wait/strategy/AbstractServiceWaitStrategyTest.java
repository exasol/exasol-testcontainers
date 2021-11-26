package com.exasol.containers.wait.strategy;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;

@ExtendWith(MockitoExtension.class)
abstract class AbstractServiceWaitStrategyTest {
    protected static final Instant AFTER_UTC = Instant.parse("2007-12-03T10:15:30.00Z");

    @Mock
    protected LogPatternDetectorFactory detectorFactoryMock;
    @Mock
    protected LogPatternDetector detectorMock;

    protected LogPatternDetectorFactory getDetectorFactory() {
        return this.detectorFactoryMock;
    }

    /**
     * Create the concrete wait strategy.
     *
     * @param afterUtc earliest time in the log after which the log message must appear
     * @return wait strategy instance
     */
    protected abstract WaitStrategy createWaitStrategy(final Instant afterUtc);

    /**
     * Set the pattern for the log filename the Detector should look for.
     *
     * @return log file pattern
     */
    protected abstract String getLogFilenamePattern();

    /**
     * Set the log pattern the detector should use.
     *
     * @return log pattern
     */
    protected abstract String getLogEntryPattern();

    @Test
    void testWaitUntilReady() throws IOException, InterruptedException {
        when(this.detectorMock.isPatternPresent()).thenReturn(true);
        assertTimeout(Duration.ofMillis(100), () -> createWaitStrategy(AFTER_UTC).waitUntilReady(null));
    }

    @Test
    void testWaitUntilReadyRetry() throws IOException, InterruptedException {
        when(this.detectorMock.isPatternPresent()).thenReturn(false, true);
        assertTimeout(Duration.ofMillis(1500), () -> createWaitStrategy(AFTER_UTC).waitUntilReady(null));
    }

    // This test can take very long, depending on the timeout configured in the tested class.
    // It is therefore recommended to reduce the timeout in the test.
    @Test
    void testWaitUntilReadyTimesOut() throws IOException, InterruptedException {
        when(this.detectorMock.isPatternPresent()).thenReturn(false);
        final WaitStrategy waitStrategy = createWaitStrategy(AFTER_UTC);
        assertThrows(ContainerLaunchException.class, () -> waitStrategy.waitUntilReady(null));
    }
}