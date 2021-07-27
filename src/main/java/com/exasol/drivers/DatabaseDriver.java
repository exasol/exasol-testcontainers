package com.exasol.drivers;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * Common interface for all database Drivers
 */
public interface DatabaseDriver extends Serializable {
    /**
     * Get the human-readable driver name.
     *
     * @return driver name
     */
    public String getName();

    /**
     * Check if a local file on the host is associated with the driver.
     *
     * @return {@code true} if a local file is associated with the driver
     */
    public boolean hasSourceFile();

    /**
     * Get the local path under which the driver file exists on the host
     *
     * @return source path
     */
    public Path getSourcePath();

    /**
     * Get the name of the driver file.
     *
     * @return file name
     */
    public String getFileName();

    /**
     * Get the driver's Exasol-specific manifest.
     *
     * @return driver manifest
     */
    public String getManifest();
}