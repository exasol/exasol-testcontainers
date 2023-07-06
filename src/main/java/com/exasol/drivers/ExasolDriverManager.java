package com.exasol.drivers;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.bucketfs.*;

/**
 * Manager for drivers for external data repositories that are installed on the Exasol Database.
 */
public class ExasolDriverManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolDriverManager.class);
    private static final String MANIFEST_FILENAME = "settings.cfg";
    static final String DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET = "drivers/jdbc";
    static final String MANIFEST_PATH_IN_BUCKET = DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET + BucketConstants.PATH_SEPARATOR
            + MANIFEST_FILENAME;
    private final Map<String, DatabaseDriver> installedDrivers = new HashMap<>();
    private final Bucket bucket;

    /**
     * Create a new instance of an {@link ExasolDriverManager}.
     *
     * @param bucket BucketFS bucket in which the drivers should be installed
     */
    public ExasolDriverManager(final Bucket bucket) {
        this.bucket = bucket;
    }

    /**
     * Install one or more drivers for external data repositories.
     *
     * @param drivers drivers to be installed
     */
    // [impl->dsn~installing-a-jdbc-driver-from-host-filesystem~1]
    public void install(final DatabaseDriver... drivers) {
        for (final DatabaseDriver driver : drivers) {
            installDriver(driver);
        }
    }

    private void installDriver(final DatabaseDriver driver) {
        registerDriver(driver);
        if (driver.hasSourceFile()) {
            LOGGER.debug("Installing driver {}", driver);
            uploadDriver(driver);
        } else {
            LOGGER.debug(
                    "Registering driver {} without uploading it. Make sure it is already present when using this feature!",
                    driver);
        }
        uploadManifest(getManifest());
    }

    private void registerDriver(final DatabaseDriver driver) {
        this.installedDrivers.put(driver.getName(), driver);
    }

    private void uploadDriver(final DatabaseDriver driver) {
        try {
            final String pathInBucket = DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET + BucketConstants.PATH_SEPARATOR
                    + driver.getFileName();
            this.bucket.uploadFile(driver.getSourcePath(), pathInBucket);
        } catch (final FileNotFoundException exception) {
            throw new DriverManagerException("Driver file not found.", driver, exception);
        } catch (final BucketAccessException | TimeoutException exception) {
            throw new DriverManagerException("Unable to upload database driver.", driver, exception);
        }
    }

    private void uploadManifest(final String manifest) {
        try {
            this.bucket.uploadStringContent(manifest, MANIFEST_PATH_IN_BUCKET);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new DriverManagerException("Interrupted during upload of database driver manifest upload to \""
                    + MANIFEST_PATH_IN_BUCKET + "\"", exception);
        } catch (final BucketAccessException | TimeoutException exception) {
            throw new DriverManagerException(
                    "Unable to upload database driver manifest to \"" + MANIFEST_PATH_IN_BUCKET + "\"", exception);
        }
    }

    /**
     * Get a list of all installed drivers.
     *
     * @return list of installed drivers
     */
    public Collection<DatabaseDriver> getDrivers() {
        return this.installedDrivers.values();
    }

    /**
     * Get the combined manifest for all installed drivers.
     *
     * @return combined drivers manifest
     */
    public String getManifest() {
        return this.installedDrivers.values() //
                .stream() //
                .map(DatabaseDriver::getManifest) //
                .collect(Collectors.joining("\n"));
    }
}