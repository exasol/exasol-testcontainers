package com.exasol.containers.wait.strategy;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;

abstract class AbstractServiceWaitStrategyTest {
    @Mock
    private LogPatternDetectorFactory detectorFactoryMock;
    @Mock
    private LogPatternDetector detectorMock;

    @BeforeEach
    void beforeEach() {
        when(this.detectorFactoryMock.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH, getLogFilenamePattern(),
                getLogEntryPattern())).thenReturn(this.detectorMock);
    }

    protected LogPatternDetectorFactory getDetectorFactory() {
        return this.detectorFactoryMock;
    }

    /**
     * Create the concrete wait strategy.
     *
     * @return wait strategy instance
     */
    protected abstract WaitStrategy createWaitStrategy();

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
        when(this.detectorMock.isPatternPresentAfter(any())).thenReturn(true);
        assertTimeout(Duration.ofMillis(100), () -> createWaitStrategy().waitUntilReady(null));
    }

    @Test
    void testWaitUntilReadyRetry() throws IOException, InterruptedException {
        when(this.detectorMock.isPatternPresentAfter(any())).thenReturn(false, true);
        assertTimeout(Duration.ofMillis(1500), () -> createWaitStrategy().waitUntilReady(null));
    }

    @Test
    void testWaitUntilReadyTimesOut() throws IOException, InterruptedException {
        when(this.detectorMock.isPatternPresentAfter(any())).thenReturn(false);
        final WaitStrategy waitStrategy = createWaitStrategy();
        assertThrows(ContainerLaunchException.class, () -> waitStrategy.waitUntilReady(null));
    }
}