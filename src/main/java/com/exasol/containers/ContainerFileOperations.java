package com.exasol.containers;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import org.testcontainers.containers.Container;

/**
 * Provides methods for accessing the container filesystem.
 */
public class ContainerFileOperations {

    private final ExasolContainer<? extends ExasolContainer<?>> container;

    /**
     * Create a new instance of the {@link ContainerFileOperations}.
     *
     * @param container Container reference required for executing commands inside the container.
     */
    public ContainerFileOperations(final ExasolContainer<? extends ExasolContainer<?>> container) {
        this.container = container;
    }

    /**
     * Reads a file from a given path.
     *
     * @param pathInContainer path inside the container
     * @param outputCharset   character set in which to read the file
     * @return the file content
     * @throws ExasolContainerException when the file is not found.
     */
    public String readFile(final String pathInContainer, final Charset outputCharset) throws ExasolContainerException {
        try {
            final Container.ExecResult result = this.container.execInContainer(outputCharset, "cat", pathInContainer);
            if (!result.getStderr().isBlank()) {
                final String errorMessage = result.getStderr().trim();
                throw new ExasolContainerException(messageBuilder("F-ETC-10")
                        .message("Unable to read file {{path}} from container. Error message: {{errorMessage}}.")
                        .parameter("path", pathInContainer, "path inside the container")
                        .parameter("errorMessage", errorMessage, "error message returned by command").toString(), null);
            }
            return result.getStdout();
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    messageBuilder("F-ETC-12").message("InterruptedException when reading file content").toString(),
                    exception);
        } catch (final IOException exception) {
            throw new UncheckedIOException(messageBuilder("F-ETC-11")
                    .message("Unable to read file {{path}} from container.", pathInContainer).toString(), exception);
        }
    }
}
