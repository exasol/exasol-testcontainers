package com.exasol.containers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import org.testcontainers.containers.Container;

/**
 * Provides methods for accessing the container filesystem.
 */
public class ContainerFileOperations {

    private final ExasolContainer<? extends ExasolContainer<?>> container;

    public ContainerFileOperations(final ExasolContainer<? extends ExasolContainer<?>> container) {
        this.container = container;
    }

    /**
     * Reads a file from a given path.
     *
     * @param pathInContainer path inside the container
     * @param outputCharset   charset in which to read the file
     * @return the file content
     * @throws ExasolContainerException when the file is not found.
     */
    public String readFile(final String pathInContainer, final Charset outputCharset) throws ExasolContainerException {
        try {
            final Container.ExecResult result = this.container.execInContainer(outputCharset, "cat", pathInContainer);
            if (!result.getStderr().isBlank()) {
                throw new ExasolContainerException(
                        "Error reading file '" + pathInContainer + "': '" + result.getStderr().trim() + "'", null);
            }
            return result.getStdout();
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("InterruptedException when reading file content", exception);
        } catch (final IOException exception) {
            throw new UncheckedIOException("Exception reading content of file '" + pathInContainer + "'", exception);
        }
    }
}
