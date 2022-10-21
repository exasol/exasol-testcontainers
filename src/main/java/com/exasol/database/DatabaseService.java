package com.exasol.database;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.exec.ExitCode;
import com.exasol.containers.ssh.SshException;

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
        LOGGER.debug("Starting database \"{}\".", this.databaseName);
        changeDatabaseState("start", "start-wait", "started");
    }

    private void changeDatabaseState(final String action, final String dwadCommand, final String resultState)
            throws InterruptedException {
        try {
            final long before = System.currentTimeMillis();
            final ExecResult result = this.container.execInContainer("dwad_client", dwadCommand, this.databaseName);
            if (result.getExitCode() == ExitCode.OK) {
                LOGGER.debug("Database \"{}\" {} {} ms after {} request.", this.databaseName, resultState,
                        System.currentTimeMillis() - before, action);
            } else {
                throw new DatabaseServiceException(this.databaseName,
                        "Attempt to " + action + " the database \"" + this.databaseName + "\" failed");
            }
        } catch (UnsupportedOperationException | SshException | IOException exception) {
            throw new DatabaseServiceException(this.databaseName, "Unable to " + action + " database service.",
                    exception);
        }
    }

    /**
     * Stop the database service.
     *
     * @throws InterruptedException if the attempt to stop the database was interrupted
     */
    // [impl->dsn~database-service-stops-the-database~1]
    public void stop() throws InterruptedException {
        LOGGER.debug("Stopping database \"{}\".", this.databaseName);
        changeDatabaseState("stop", "stop-wait", "stopped");
    }
}