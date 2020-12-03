package com.exasol.drivers;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;

/**
 * Manager for drivers for external data repositories that are installed on the Exasol Database.
 */
public class ExasolDriverManager {
    private static final String MANIFEST_FILENAME = "settings.cfg";
    static final String DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET = "drivers/jdbc";
    static final String MANIFEST_PATH_IN_BUCKET = DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET + "/" + MANIFEST_FILENAME;
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
            uploadDriver(driver);
        }
        uploadManifest(getManifest());
    }

    private void registerDriver(final DatabaseDriver driver) {
        this.installedDrivers.put(driver.getName(), driver);
    }

    private void uploadDriver(final DatabaseDriver driver) {
        try {
            this.bucket.uploadFile(driver.getSourcePath(), DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET + "/");
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new DriverManagerException("Interrupted during database driver upload.", driver, exception);
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
                .map(DatabaseDriver::getManifest)//
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * Uninstall one or more drivers for external data repositories.
     *
     * @param drivers to be uninstalled
     */
    // [impl->dsn~uninstalling-a-jdbc-driver-from-host-filesystem~1]
    public void uninstall(final DatabaseDriver... drivers) {
        for (final DatabaseDriver driver : drivers) {
            uninstallDriver(driver);
        }

    }

    private void uninstallDriver(final DatabaseDriver driver) {
        this.installedDrivers.remove(driver.getName());
    }
}