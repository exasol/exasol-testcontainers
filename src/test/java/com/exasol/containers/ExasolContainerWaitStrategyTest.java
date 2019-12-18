package com.exasol.containers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.wait.LogFileEntryWaitStrategy;

@Testcontainers
class ExasolContainerWaitStrategyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainerWaitStrategyTest.class);

    @Container
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>(
            ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE) //
                    .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    @Test
    void testWaitForNonExistingLogTimesOut() {
        final WaitStrategy strategy = new LogFileEntryWaitStrategy(container, "/non/existing/log/", "file", ".*");
        assertThrows(ContainerLaunchException.class, () -> strategy.waitUntilReady(container));
    }

    @Test
    void testWatingSucceedsIfExpectedLogMessageAppears() {
        final String expectedMessage = "ping";
        final String logFileName = "test.log";
        final WaitStrategy strategy = new LogFileEntryWaitStrategy(container, ExasolContainerConstants.EXASOL_LOGS_PATH,
                logFileName, expectedMessage);
        final Thread writerThread = new Thread(new MessageWriter(logFileName, expectedMessage));
        writerThread.run();
        assertDoesNotThrow(() -> strategy.waitUntilReady(ExasolContainerWaitStrategyTest.container));
    }

    private static final class MessageWriter implements Runnable {
        private final String logFilePath;
        private final String message;

        public MessageWriter(final String logFilePath, final String expectedMessage) {
            this.logFilePath = logFilePath;
            this.message = expectedMessage;
        }

        @Override
        @SuppressWarnings("squid:S2925") // we need a sleep() here to make sure the trigger comes after waiting.
        public void run() {
            try {
                writeMessage("no effect");
                Thread.sleep(2000);
                writeMessage(this.message);
            } catch (final InterruptedException exception) {
                Thread.interrupted();
            } catch (final IOException exeception) {
                throw new RuntimeException("Unable to write trigger log message to \"" + this.logFilePath + "\".");
            }
        }

        private void writeMessage(final String message2) throws IOException, InterruptedException {
            container.execInContainer("echo", message2, ">>", this.logFilePath);
        }
    }
}