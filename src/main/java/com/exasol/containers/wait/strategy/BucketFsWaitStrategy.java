package com.exasol.containers.wait.strategy;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.clusterlogs.LogPatternDetectorFactory;

/**
 * Strategy for waiting until the Bucket Filesystem is ready.
 */
public class BucketFsWaitStrategy extends LogFileEntryWaitStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(BucketFsWaitStrategy.class);
    static final String BUCKETFS_READY_PATTERN = "UNPACK THREAD started$";

    /**
     * Create a new instance of a {@link BucketFsWaitStrategy}.
     *
     * @param detectorFactory factory for log message pattern detectors
     */
    public BucketFsWaitStrategy(final LogPatternDetectorFactory detectorFactory) {
        super(detectorFactory, EXASOL_CORE_DAEMON_LOGS_PATH, BUCKETFS_DAEMON_LOG_FILENAME_PATTERN,
                BUCKETFS_READY_PATTERN);
    }

    @Override
    protected void waitUntilReady() {
        LOGGER.info("Waiting for BucketFS to be ready.");
        super.waitUntilReady();
        LOGGER.info("BucketFS is ready.");
    }
}