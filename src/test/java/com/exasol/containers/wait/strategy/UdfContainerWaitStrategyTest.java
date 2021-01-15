package com.exasol.containers.wait.strategy;

import java.time.Instant;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import com.exasol.containers.ExasolContainerConstants;

@Tag("slow")
@ExtendWith(MockitoExtension.class)
class UdfContainerWaitStrategyTest extends AbstractServiceWaitStrategyTest {
    @Override
    protected WaitStrategy createWaitStrategy() {
        return new UdfContainerWaitStrategy(getDetectorFactory(), Instant.now());
    }

    @Override
    protected String getLogEntryPattern() {
        return UdfContainerWaitStrategy.SCRIPT_LANGUAGE_CONTAINER_READY_PATTERN;
    }

    @Override
    protected String getLogFilenamePattern() {
        return ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
    }
}