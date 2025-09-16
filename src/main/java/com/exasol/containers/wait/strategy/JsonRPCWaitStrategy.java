package com.exasol.containers.wait.strategy;

import com.exasol.clusterlogs.LogPatternDetectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;

public class JsonRPCWaitStrategy extends LogFileEntryWaitStrategy{
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonRPCWaitStrategy.class);
    static final String JSON_RPC_READY_PATTERN = ""; // TODO

    public JsonRPCWaitStrategy(final LogPatternDetectorFactory detectorFactory) {
        super(detectorFactory.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, JSON_RPC_READY_PATTERN));
    }

    @Override
    protected void waitUntilReady() {
        LOGGER.debug("Waiting {} for JsonRPC to be ready.", this.startupTimeout);
        final Instant start = Instant.now();
        super.waitUntilReady();
        LOGGER.debug("JsonRPC is ready after {}.", Duration.between(start, Instant.now()));
    }
}
