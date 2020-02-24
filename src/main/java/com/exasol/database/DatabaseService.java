package com.exasol.database;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.exec.ExitCode;

/**
 * Controller for a database service.
 */
public class DatabaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);
    private final String databaseName;
    private final Container<? extends Container<?>> container;

    /**
     * Create a new instance of e {@link DatabaseService}.
     *
     * @param databaseName name of the database provided by this service
     * @param container    container the database runs in
     */
    public DatabaseService(final String databaseName, final Container<? extends Container<?>> container) {
        this.databaseName = databaseName;
        this.container = container;
    }

    /**
     * Get the name of the database provide by this service
     *
     * @return database name
     */
    public String getDatabaseName() {
        return this.databaseName;
    }

    /**
     * Start the database service.
     *
     * @throws InterruptedException if starting the database got interrupted
     */
    // [impl->dsn~database-service-starts-the-database~1]
    public void start() throws InterruptedException {
        LOGGER.info("Starting database \"{}\".", this.databaseName);
        try {
            final long before = System.currentTimeMillis();
            final ExecResult result = this.container.execInContainer("dwad_client", "start-wait", this.databaseName);
            if (result.getExitCode() == ExitCode.OK) {
                LOGGER.info("Database \"{}\" started {} ms after start request.", this.databaseName,
                        System.currentTimeMillis() - before);
            } else {
                throw new DatabaseServiceException(this.databaseName,
                        "Attempt to start the database \"" + this.databaseName + "\" failed");
            }
        } catch (UnsupportedOperationException | IOException exception) {
            throw new DatabaseServiceException(this.databaseName, "Unable to start database service.", exception);
        }
    }

    /**
     * Stop the database service.
     *
     * @throws InterruptedException if the attempt to stop the database was interrupted
     */
    // [impl->dsn~database-service-stops-the-database~1]
    public void stop() throws InterruptedException {
        LOGGER.info("Stopping database \"{}\".", this.databaseName);
        try {
            final long before = System.currentTimeMillis();
            final ExecResult result = this.container.execInContainer("dwad_client", "stop-wait", this.databaseName);
            if (result.getExitCode() == ExitCode.OK) {
                LOGGER.info("Database \"{}\" stopped {} ms after stop request.", this.databaseName,
                        System.currentTimeMillis() - before);
            } else {
                throw new DatabaseServiceException(this.databaseName,
                        "Attempt to stop the database \"" + this.databaseName + "\" failed");
            }
        } catch (UnsupportedOperationException | IOException exception) {
            throw new DatabaseServiceException(this.databaseName, "Unable to stop database service.", exception);
        }
    }
}