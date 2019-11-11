package com.exasol.containers;

/**
 * Exception for Exasol test containers
 */
public class ExasolContainerException extends Exception {
    private static final long serialVersionUID = 1350357371874161519L;

    /**
     * Create a new instance of an {@link ExasolContainerException}
     *
     * @param message error message
     * @param cause   exception causing this one
     */
    public ExasolContainerException(final String message, final Exception cause) {
        super(message, cause);
    }
}