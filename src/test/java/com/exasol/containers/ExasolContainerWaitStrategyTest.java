package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_LOGS_PATH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.wait.strategy.LogFileEntryWaitStrategy;

@Testcontainers
class ExasolContainerWaitStrategyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainerWaitStrategyTest.class);

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
                new MessageWriter(EXASOL_LOGS_PATH + "/" + logFileName, expectedMessage));
        writerThread.start();
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

        private void writeMessage(final String messageToAppend) throws IOException, InterruptedException {
            final String timestamp = DateTimeFormatter.ofPattern("yyMMdd HH:mm:ss")
                    .format(LocalDateTime.now(ZoneId.of("UTC")));
            final String logEntry = "[I " + timestamp + " somedaemon:1234] " + messageToAppend;
            LOGGER.info("Writing log message: " + logEntry);
            final ExecResult result = writeViaDockerExec(logEntry);
            if (result.getExitCode() != 0) {
                throw new IllegalStateException("Writer thread returned non-zero exit code.");
            }
        }

        private ExecResult writeViaDockerExec(final String logEntry) throws IOException, InterruptedException {
            // Wrapping the echo in a shell is necessary to enable redirection
            return container.execInContainer("sh", "-c", "echo " + logEntry + " >> " + this.logFilePath);
        }
    }
}