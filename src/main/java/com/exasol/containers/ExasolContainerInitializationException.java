package com.exasol.containers;

/**
 * Exception for Exasol test containers.
 */
public class ExasolContainerInitializationException extends RuntimeException {
    private static final long serialVersionUID = -6118716444353339342L;

    /**
     * Create a new instance of an {@link ExasolContainerInitializationException}.
     *
     * @param message error message
     * @param cause   exception causing this one
     */
    public ExasolContainerInitializationException(final String message, final Exception cause) {
        super(message, cause);
    }
}
