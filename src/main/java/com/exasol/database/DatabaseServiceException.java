package com.exasol.database;

/**
 * Exception for database services.
 */
public class DatabaseServiceException extends RuntimeException {
    private static final long serialVersionUID = 7564249305493337072L;
    /** @serial */
    private final String databaseName;

    /**
     * Create a new instance of a {@link DatabaseServiceException}.
     *
     * @param databaseName name of the affected database
     * @param message      error message
     * @param cause        exception that cause this one
     */
    public DatabaseServiceException(final String databaseName, final String message, final Exception cause) {
        super(message, cause);
        this.databaseName = databaseName;
    }

    /**
     * Create a new instance of a {@link DatabaseServiceException}.
     *
     * @param databaseName name of the affected database
     * @param message      error message
     */
    public DatabaseServiceException(final String databaseName, final String message) {
        super(message);
        this.databaseName = databaseName;
    }

    /**
     * Get the name of the affected database
     *
     * @return name of the affected database
     */
    public String getDatabaseName() {
        return this.databaseName;
    }
}