package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_LOGS_PATH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.bucketfs.monitor.TimestampRetriever;
import com.exasol.containers.wait.strategy.LogFileEntryWaitStrategy;

@Tag("slow")
@Testcontainers
class ExasolContainerWaitStrategyIT {
    @Container
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>();

    @Test
    void testWaitForNonExistingLogTimesOut() {

        final WaitStrategy strategy = new LogFileEntryWaitStrategy(container.getLogPatternDetectorFactory(),
                "/non/existing/log/", "file", ".*", new TimestampRetriever().getState());
        assertThrows(ContainerLaunchException.class, () -> strategy.waitUntilReady(container));
    }

    @Test
    void testWatingSucceedsIfExpectedLogMessageAppears() {
        final String expectedMessage = "ping";
        final String logFileName = "test.log";
        final WaitStrategy strategy = new LogFileEntryWaitStrategy(container.getLogPatternDetectorFactory(),
                EXASOL_LOGS_PATH, logFileName, expectedMessage, new TimestampRetriever().getState());
        final Thread writerThread = new Thread(
                new MessageWriter(container, EXASOL_LOGS_PATH + "/" + logFileName, expectedMessage));
        writerThread.start();
        assertDoesNotThrow(() -> strategy.waitUntilReady(container));
    }
}