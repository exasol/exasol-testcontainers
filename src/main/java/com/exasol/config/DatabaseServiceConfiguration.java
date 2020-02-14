package com.exasol.config;

/**
 * Configuration parameters of a database service.
 */
public class DatabaseServiceConfiguration {
    private final String databaseName;

    /**
     * Create a new instance of a {@link DatabaseServiceConfiguration}.
     *
     * @param builder builder for the service configuration
     */
    public DatabaseServiceConfiguration(final Builder builder) {
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
     * Create a builder for the {@link DatabaseServiceConfiguration}.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link DatabaseServiceConfiguration}.
     */
    public static class Builder {
        private String databaseName;

        /**
         * Set the name of the database provided by the service.
         *
         * @param databaseName name of the database
         * @return builder instance for fluent programming
         */
        public Builder databaseName(final String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        /**
         * Build a new {@link DatabaseServiceConfiguration} instance.
         *
         * @return {@link DatabaseServiceConfiguration} instance
         */
        public DatabaseServiceConfiguration build() {
            return new DatabaseServiceConfiguration(this);
        }
    }
}