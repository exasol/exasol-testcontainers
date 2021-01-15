package com.exasol.containers.wait.strategy;

import java.time.Instant;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import com.exasol.containers.ExasolContainerConstants;

@Tag("slow")
@ExtendWith(MockitoExtension.class)
class BucketFsWaitStrategyTest extends AbstractServiceWaitStrategyTest {
    @Override
    protected WaitStrategy createWaitStrategy() {
        return new BucketFsWaitStrategy(getDetectorFactory(), Instant.now());
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