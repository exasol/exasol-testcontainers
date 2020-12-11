package com.exasol.containers.workarounds;

/**
 * Exception thrown while trying to apply a workaround.
 */
public class WorkaroundException extends Exception {
    private static final long serialVersionUID = 3637242640868814029L;

    /**
     * Create a new instance of a {@link WorkaroundException}.
     *
     * @param message error message
     */
    public WorkaroundException(final String message) {
        super(message);
    }

    /**
     * Create a new instance of a {@link WorkaroundException}.
     *
     * @param message error message
     * @param cause   exception causing this one
     */
    public WorkaroundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}