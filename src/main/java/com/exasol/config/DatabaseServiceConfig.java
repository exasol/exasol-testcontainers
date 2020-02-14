package com.exasol.config;

/**
 * Configuration parameters of a database service.
 */
public class DatabaseServiceConfig {
    private final String databaseName;

    /**
     * Create a new instance of a {@link DatabaseServiceConfig}.
     *
     * @param builder builder for the service configuration
     */
    public DatabaseServiceConfig(final Builder builder) {
        this.databaseName = builder.databaseName;
    }

    /**
     * Get the name of the database provided by this service.
     *
     * @return database name
     */
    public String getDatabaseName() {
        return this.databaseName;
    }

    /**
     * Create a builder for the {@link DatabaseServiceConfig}.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link DatabaseServiceConfig}.
     */
    public static class Builder {
        private String databaseName;

        /**
         * Set the name of the database provide by the service.
         *
         * @param databaseName name of the database
         * @return builder instance for fluent programming
         */
        public Builder databaseName(final String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        /**
         * Build a new {@link DatabaseServiceConfig} instance.
         *
         * @return {@link DatabaseServiceConfig} instance
         */
        public DatabaseServiceConfig build() {
            return new DatabaseServiceConfig(this);
        }
    }
}