package com.exasol.containers.status;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache that remembers the container status across multiple test runs when the container is reused.
 */
public class ContainerStatusCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerStatusCache.class);
    static final String CACHE_FILE_EXTENSION = ".cache";
    private final Path cacheDirectory;

    /**
     * Create a new instance of a {@link ContainerStatusCache}.
     *
     * @param tempDirectory temporary directory where cache files are stored
     */
    public ContainerStatusCache(final Path tempDirectory) {
        this.cacheDirectory = tempDirectory;
    }

    /**
     * Get the directory in which the cache files are stored.
     *
     * @return cache directory
     */
    public Path getCacheDirectory() {
        return this.cacheDirectory;
    }

    /**
     * Check if a cache for a given container ID is available
     *
     * @param containerId docker container ID
     * @return {@code true} if a cache is available for this container
     */
    public boolean isCacheAvailable(final String containerId) {
        final Path cacheFile = getCacheFileForContainer(containerId);
        return Files.exists(cacheFile);
    }

    private Path getCacheFileForContainer(final String containerId) {
        return this.cacheDirectory.resolve(containerId + CACHE_FILE_EXTENSION);
    }

    /**
     * Get the container state cache for the given container ID.
     *
     * @param containerId ID of the docker container for which the cache is requested.
     * @return container state cache
     */
    public ContainerStatus read(final String containerId) {
        return readFromCacheFile(getCacheFileForContainer(containerId));
    }

    private ContainerStatus readFromCacheFile(final Path cacheFile) {
        LOGGER.debug("Reading container state from cache file \"{}\".", cacheFile);
        try (final FileInputStream inputStream = new FileInputStream(cacheFile.toFile()); //
                final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream) //
        ) {
            return (ContainerStatus) objectInputStream.readObject();
        } catch (final IOException | ClassNotFoundException exception) {
            throw new ContainerStatusCacheException(
                    "Unable to read container state from cache file '" + cacheFile + "'.", exception);
        }
    }

    /**
     * Write the container state to the cache.
     *
     * @param containerId     ID of the docker container for which a cache entry is written
     * @param containerStatus container state to be cached
     */
    public void write(final String containerId, final ContainerStatus containerStatus) {
        createMissingCacheDirectory();
        writeToFile(getCacheFileForContainer(containerId), containerStatus);
    }

    private void createMissingCacheDirectory() {
        if (!Files.exists(this.cacheDirectory)) {
            LOGGER.debug("Container status directory \"{}\" does not exist. Creating.", this.cacheDirectory);
            try {
                Files.createDirectories(this.cacheDirectory);
            } catch (final IOException exception) {
                throw new ContainerStatusCacheException(
                        "Unable to container status cache directory \"" + this.cacheDirectory + "\".", exception);
            }
        }
    }

    private void writeToFile(final Path cacheFile, final ContainerStatus containerStatus) {
        LOGGER.debug("Writing container status to cache file \"{}\".", cacheFile);
        try (final FileOutputStream outputStream = new FileOutputStream(cacheFile.toFile()); //
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream) //
        ) {
            objectOutputStream.writeObject(containerStatus);
        } catch (final IOException exception) {
            throw new ContainerStatusCacheException(
                    "Unable to write container status to cache file \"" + cacheFile + "\".", exception);
        }
    }
}
