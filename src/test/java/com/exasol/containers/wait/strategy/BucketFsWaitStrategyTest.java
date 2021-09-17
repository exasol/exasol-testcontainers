package com.exasol.containers.wait.strategy;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import com.exasol.containers.ExasolContainerConstants;

@ExtendWith(MockitoExtension.class)
class BucketFsWaitStrategyTest extends AbstractServiceWaitStrategyTest {

    @BeforeEach
    void beforeEach() {
        when(this.detectorFactoryMock.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH, getLogFilenamePattern(),
                getLogEntryPattern())).thenReturn(this.detectorMock);
    }

    @Override
    protected WaitStrategy createWaitStrategy(final Instant afterUtc) {
        return new BucketFsWaitStrategy(getDetectorFactory());
    }

    @Override
    protected String getLogEntryPattern() {
        return BucketFsWaitStrategy.BUCKETFS_READY_PATTERN;
    }

    @Override
    protected String getLogFilenamePattern() {
        return ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
    }
}