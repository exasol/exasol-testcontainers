package com.exasol.containers.wait.strategy;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.clusterlogs.LogPatternDetectorFactory;

/**
 * Strategy for waiting until the UDF language container is ready.
 */
public class UdfContainerWaitStrategy extends LogFileEntryWaitStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(UdfContainerWaitStrategy.class);
    public static final String SCRIPT_LANGUAGE_CONTAINER_READY_PATTERN = "ScriptLanguages.*extracted$";

    /**
     * Create a new instance of a {@link UdfContainerWaitStrategy}.
     *
     * @param detectorFactory factory for log message pattern detectors
     * @param afterUtc        earliest time after which the UDF container must report readiness
     */
    public UdfContainerWaitStrategy(final LogPatternDetectorFactory detectorFactory, final Instant afterUtc) {
        super(detectorFactory, EXASOL_CORE_DAEMON_LOGS_PATH, BUCKETFS_DAEMON_LOG_FILENAME_PATTERN,
                SCRIPT_LANGUAGE_CONTAINER_READY_PATTERN, afterUtc);
    }

    @Override
    protected void waitUntilReady() {
        LOGGER.debug("Waiting for UDF language container to be ready.");
        super.waitUntilReady();
        LOGGER.debug("UDF language container is ready.");
    }
}