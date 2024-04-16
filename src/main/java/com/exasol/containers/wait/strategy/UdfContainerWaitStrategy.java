package com.exasol.containers.wait.strategy;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.clusterlogs.LogPatternDetectorFactory;

/**
 * Strategy for waiting until the UDF language container is ready.
 */
public class UdfContainerWaitStrategy extends LogFileEntryWaitStrategy {
    /** Pattern for detecting extraction of script language containers */
    public static final String SCRIPT_LANGUAGE_CONTAINER_READY_PATTERN = "ScriptLanguages.*extracted$";
    private static final long WAIT_FOR_UDF_CONTAINER_DURATION_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(10);
    private static final Logger LOGGER = LoggerFactory.getLogger(UdfContainerWaitStrategy.class);

    /**
     * Create a new instance of a {@link UdfContainerWaitStrategy}.
     *
     * @param detectorFactory factory for log message pattern detectors
     */
    public UdfContainerWaitStrategy(final LogPatternDetectorFactory detectorFactory) {
        super(detectorFactory.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, SCRIPT_LANGUAGE_CONTAINER_READY_PATTERN));
    }

    @Override
    protected void waitUntilReady() {
        LOGGER.debug("Waiting {} for UDF language container to be ready.",
                Duration.ofMillis(getWaitTimeOutMilliseconds()));
        final Instant start = Instant.now();
        super.waitUntilReady();
        LOGGER.debug("UDF language container is ready after {}.", Duration.between(start, Instant.now()));
    }

    @Override
    protected long getWaitTimeOutMilliseconds() {
        return WAIT_FOR_UDF_CONTAINER_DURATION_IN_MILLISECONDS;
    }
}
