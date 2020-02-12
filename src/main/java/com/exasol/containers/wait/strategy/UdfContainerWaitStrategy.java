package com.exasol.containers.wait.strategy;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.clusterlogs.LogPatternDetectorFactory;

/**
 * Strategy for waiting until the UDF language container is ready.
 */
public class UdfContainerWaitStrategy extends LogFileEntryWaitStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(UdfContainerWaitStrategy.class);
    static final String SCRIPT_LANGUAGE_CONTAINER_READY_PATTERN = "ScriptLanguages.*extracted$";

    /**
     * Create a new instance of a {@link UdfContainerWaitStrategy}.
     *
     * @param detectorFactory factory for log message pattern detectors
     */
    public UdfContainerWaitStrategy(final LogPatternDetectorFactory detectorFactory) {
        super(detectorFactory, EXASOL_CORE_DAEMON_LOGS_PATH, BUCKETFS_DAEMON_LOG_FILENAME_PATTERN,
                SCRIPT_LANGUAGE_CONTAINER_READY_PATTERN);
    }

    @Override
    protected void waitUntilReady() {
        LOGGER.info("Waiting for UDF language container to be ready.");
        super.waitUntilReady();
        LOGGER.info("UDF language container is ready.");
    }
}