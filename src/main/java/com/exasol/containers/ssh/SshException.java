package com.exasol.containers.ssh;

/**
 * Special exception for SSH API.
 */
public class SshException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Create new instance of {@link SshException}
     *
     * @param message message
     */
    public SshException(final String message) {
        super(message);
    }

    /**
     * Create new instance of {@link SshException}
     *
     * @param exception causing the current instance of {@link SshException}
     */
    public SshException(final Exception cause) {
        super(cause);
    }
}