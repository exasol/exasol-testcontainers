package com.exasol.containers.ssh;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Considers a number of candidates for a directory with respect to specific conditions for each candidate.
 */
public class DirectorySelector {

    private final Path parent;

    private boolean found = false;
    private Path path;
    private boolean knownAsExisting = false;

    private static final Path CURRENT_WORKING_DIRECTORY = Path.of("");

    public DirectorySelector() {
        this(CURRENT_WORKING_DIRECTORY);
    }

    public DirectorySelector(final Path parent) {
        this.parent = parent;
    }

    public DirectorySelector ifNotNull(final Path path) {
        return consider(path != null, path, false);
    }

    public DirectorySelector orIfExists(final String name) {
        final Path path = this.parent.resolve(name);
        return consider(Files.isDirectory(path), path, true);
    }

    public DirectorySelector or(final String name) {
        return consider(true, this.parent.resolve(name), false);
    }

    private DirectorySelector consider(final boolean condition, final Path path, final boolean knownAsExisting) {
        if (condition && !this.found) {
            this.found = true;
            this.path = path;
            this.knownAsExisting = knownAsExisting;
        }
        return this;
    }

    Path getPath() {
        return this.path;
    }

    public Path ensureExists() throws UncheckedIOException {
        if (this.knownAsExisting || Files.isDirectory(this.path)) {
            return this.path;
        }
        try {
            return Files.createDirectories(this.path);
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
