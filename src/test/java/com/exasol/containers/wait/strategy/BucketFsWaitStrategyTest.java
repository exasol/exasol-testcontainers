package com.exasol.containers.wait.strategy;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;

@ExtendWith(MockitoExtension.class)
class BucketFsWaitStrategyTest {
    @Mock
    private LogPatternDetectorFactory detectorFactoryMock;
    @Mock
    private LogPatternDetector detectorMock;

    @BeforeEach
    void beforeEach() {
        when(this.detectorFactoryMock.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, BucketFsWaitStrategy.BUCKETFS_READY_PATTERN))
                        .thenReturn(this.detectorMock);
    }

    @Test
    void testWaitUntilReady() throws IOException, InterruptedException {
        when(this.detectorMock.isPatternPresentAfter(any())).thenReturn(true);
        assertTimeout(Duration.ofMillis(100), () -> createWaitStrategy().waitUntilReady(null));
    }

    private WaitStrategy createWaitStrategy() {
        return new BucketFsWaitStrategy(this.detectorFactoryMock);
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