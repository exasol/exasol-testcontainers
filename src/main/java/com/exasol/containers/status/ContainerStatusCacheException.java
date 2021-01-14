package com.exasol.containers.status;

/**
 * This class represents error occurring during reading from and writing to the service state cache.
 */
public class ContainerStatusCacheException extends RuntimeException {
    private static final long serialVersionUID = -1064530490465470592L;

    /**
     * Create a new instance of a {@link ContainerStatusCacheException}.
     *
     * @param message error message
     */
    public ContainerStatusCacheException(final String message) {
        super(message);
    }

    /**
     * Create a new instance of a {@link ContainerStatusCacheException}.
     *
     * @param message error message
     * @param cause   exception that caused this one
     */
    public ContainerStatusCacheException(final String message, final Throwable cause) {
        super(message, cause);
    }
}