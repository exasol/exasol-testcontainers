package com.exasol.containers;

/**
 * Exception for Exasol test containers.
 */
public class ExasolContainerException extends RuntimeException {
    private static final long serialVersionUID = 1350357371874161519L;

    /**
     * Wrap an exception in an {@link ExasolContainerException}.
     *
     * @param exception exception to be wrapped
     */
    public ExasolContainerException(final Throwable exception) {
        super(exception);
    }

    /**
     * Create a new instance of an {@link ExasolContainerException}.
     *
     * @param message error message
     * @param cause   exception causing this one
     */
    public ExasolContainerException(final String message, final Exception cause) {
        super(message, cause);
    }

    /**
     * Create a new instance of an {@link ExasolContainerException}.
     *
     * @param message error message
     */
    public ExasolContainerException(final String message) {
        super(message);
    }
}
