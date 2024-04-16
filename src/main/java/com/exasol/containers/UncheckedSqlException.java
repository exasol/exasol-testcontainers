package com.exasol.containers;

import java.sql.SQLException;

/**
 * This runtime exception is thrown when a checked {@link SQLException} occurs. It is used to avoid checked exceptions
 * in client code.
 */
public class UncheckedSqlException extends RuntimeException {
    private static final long serialVersionUID = -6820991299847567000L;

    /**
     * Create a new instance of {@link UncheckedSqlException}.
     * 
     * @param message error message
     * @param cause   cause of the exception
     */
    public UncheckedSqlException(final String message, final SQLException cause) {
        super(message, cause);
    }
}
