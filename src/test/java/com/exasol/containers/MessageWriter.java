package com.exasol.containers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;

@Tag("fast")
final class MessageWriter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageWriter.class);
    private final ExasolContainer<? extends ExasolContainer<?>> container;
    private final String logFilePath;
    private final String message;

    public MessageWriter(final ExasolContainer<? extends ExasolContainer<?>> container, final String logFilePath,
            final String expectedMessage) {
        this.container = container;
        this.logFilePath = logFilePath;
        this.message = expectedMessage;
    }

    @Override
    @SuppressWarnings("squid:S2925") // we need a sleep() here to make sure the trigger comes after waiting.
    public void run() {
        try {
            Thread.sleep(1000);
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
        final LocalDateTime now = LocalDateTime.now(this.container.getTimeZone().toZoneId());
        final String timestamp = DateTimeFormatter.ofPattern("yyMMdd HH:mm:ss").format(now);
        final String logEntry = "[I " + timestamp + " somedaemon:1234] " + messageToAppend;
        LOGGER.info("Writing log message: " + logEntry);
        final ExecResult result = writeViaDockerExec(logEntry);
        if (result.getExitCode() != 0) {
            throw new IllegalStateException("Writer thread returned non-zero exit code.");
        }
    }

    private ExecResult writeViaDockerExec(final String logEntry) throws IOException, InterruptedException {
        // Wrapping the echo in a shell is necessary to enable redirection
        return this.container.execInContainer("sh", "-c", "echo " + logEntry + " >> " + this.logFilePath);
    }
}