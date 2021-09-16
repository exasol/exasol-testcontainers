package com.exasol.containers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import org.testcontainers.containers.Container;

public class ContainerFileOperations {

    private final ExasolContainer<? extends ExasolContainer<?>> container;

    public ContainerFileOperations(final ExasolContainer<? extends ExasolContainer<?>> container) {
        this.container = container;
    }

    public String readFile(final String pathInContainer, final Charset outputCharset) {
        try {
            final Container.ExecResult result = this.container.execInContainer(outputCharset, "cat", pathInContainer);
            if (!result.getStderr().isBlank()) {
                throw new IllegalStateException(
                        "Error reading file '" + pathInContainer + "': '" + result.getStderr().trim() + "'");
            }
            return result.getStdout();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("InterruptedException when reading file content", e);
        } catch (final IOException e) {
            throw new UncheckedIOException("Exception reading content of file '" + pathInContainer + "'", e);
        }
    }
}
