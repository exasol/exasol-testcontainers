package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_LOGS_PATH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.wait.strategy.LogFileEntryWaitStrategy;

@Testcontainers
class ExasolContainerWaitStrategyIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainerWaitStrategyIT.class);

    @Container
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>(
            EXASOL_DOCKER_IMAGE_REFERENCE) //
                    .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    @Test
    void testWaitForNonExistingLogTimesOut() {
        final WaitStrategy strategy = new LogFileEntryWaitStrategy(container.getLogPatternDetectorFactory(),
                "/non/existing/log/", "file", ".*");
        assertThrows(ContainerLaunchException.class, () -> strategy.waitUntilReady(container));
    }

    @Test
    void testWatingSucceedsIfExpectedLogMessageAppears() {
        final String expectedMessage = "ping";
        final String logFileName = "test.log";
        final WaitStrategy strategy = new LogFileEntryWaitStrategy(container.getLogPatternDetectorFactory(),
                EXASOL_LOGS_PATH, logFileName, expectedMessage);
        final Thread writerThread = new Thread(
                new MessageWriter(container, EXASOL_LOGS_PATH + "/" + logFileName, expectedMessage));
        writerThread.start();
        assertDoesNotThrow(() -> strategy.waitUntilReady(container));
    }
}