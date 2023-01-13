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

    /**
     * Create a new instance of {@link DirectorySelector}.
     */
    public DirectorySelector() {
        this(CURRENT_WORKING_DIRECTORY);
    }

    DirectorySelector(final Path parent) {
        this.parent = parent;
    }

    /**
     * Add a candidate path if path is not null.
     *
     * @param path path to add
     * @return this for fluent programming
     */
    public DirectorySelector ifNotNull(final Path path) {
        return consider(path != null, path, false);
    }

    /**
     * Add a path relative to current working directory as candidate if the path exists.
     *
     * @param name name of the directory relative to current working directory
     * @return this for fluent programming
     */
    public DirectorySelector orIfExists(final String name) {
        final Path path = this.parent.resolve(name);
        return consider(Files.isDirectory(path), path, true);
    }

    /**
     * Add a path relative to current working directory as candidate unconditionally. Method {@link #ensureExists()}
     * will create this path if it does not exist.
     *
     * @param name name of the directory relative to current working directory
     * @return this for fluent programming
     */
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

    /**
     * Ensure that the current candidate for directory selection exists. If it not known to have been existing before
     * and does not exist yet then create the directory including all parent directories and return the path.
     *
     * @return instance of {@link Path} pointing to the existing directory
     * @throws UncheckedIOException in case the creation of the path failed
     */
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
