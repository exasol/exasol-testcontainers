package com.exasol.containers.wait.strategy;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.containers.ExasolContainerConstants;

@ExtendWith(MockitoExtension.class)
class UdfContainerWaitStrategyTest extends AbstractServiceWaitStrategyTest {
    @BeforeEach
    void beforeEach() {
        when(this.detectorFactoryMock.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH, getLogFilenamePattern(),
                getLogEntryPattern())).thenReturn(this.detectorMock);
    }

    @Override
    protected WaitStrategy createWaitStrategy(final Instant afterUtc) {
        return new ShortTimeoutUdfContainerWaitStrategy(getDetectorFactory());
    }

    @Override
    protected String getLogEntryPattern() {
        return UdfContainerWaitStrategy.SCRIPT_LANGUAGE_CONTAINER_READY_PATTERN;
    }

    @Override
    protected String getLogFilenamePattern() {
        return ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
    }

    /**
     * This class is used to reduce the timeout and speed up the tests.
     */
    static class ShortTimeoutUdfContainerWaitStrategy extends UdfContainerWaitStrategy {
        public ShortTimeoutUdfContainerWaitStrategy(final LogPatternDetectorFactory detectorFactory) {
            super(detectorFactory);
        }

        @Override
        protected long getWaitTimeOutMilliseconds() {
            return 2000;
        }
    }
}