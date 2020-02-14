package com.exasol.database;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.Container;

import com.exasol.config.ClusterConfiguration;

/**
 * Factory for database services.
 */
public final class DatabaseServiceFactory {
    private final Map<String, DatabaseService> services = new HashMap<>();
    private final ClusterConfiguration clusterConfiguration;
    private final Container<? extends Container<?>> container;

    /**
     * Create a new instance of a {@link DatabaseServiceFactory}.
     *
     * @param container            container the database runs in
     * @param clusterConfiguration cluster configuration needed to determine which databases exist and how they are
     *                             configured
     */
    public DatabaseServiceFactory(final Container<? extends Container<?>> container,
            final ClusterConfiguration clusterConfiguration) {
        this.container = container;
        this.clusterConfiguration = clusterConfiguration;
    }

    /**
     * Get a database service.
     *
     * @param databaseName name of the database to get the service for
     * @return database service
     */
    public DatabaseService getDatabaseService(final String databaseName) {
        if (this.services.containsKey(databaseName)) {
            return this.services.get(databaseName);
        } else {
            if (this.clusterConfiguration.containsDatabaseService(databaseName)) {
                final DatabaseService service = new DatabaseService(databaseName, this.container);
                this.services.put(databaseName, service);
                return service;
            } else {
                throw new IllegalArgumentException(
                        "Database service \"" + databaseName + "\" does not exist. Pick one of: "
                                + String.join(", ", this.clusterConfiguration.getDatabaseNames()));
            }
        }
    }
}