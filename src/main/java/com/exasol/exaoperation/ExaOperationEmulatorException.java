package com.exasol.exaoperation;

/**
 * Exception for the emulator of EXAoperation.
 */
public class ExaOperationEmulatorException extends RuntimeException {
    private static final long serialVersionUID = 6337985652621939631L;

    /**
     * Create a new instance of the {@link ExaOperationEmulatorException}.
     *
     * @param message error message
     */
    public ExaOperationEmulatorException(final String message) {
        super(message);
    }

    /**
     * Create a new instance of the {@link ExaOperationEmulatorException}.
     *
     * @param message error message
     * @param cause   exception causing this one
     */
    public ExaOperationEmulatorException(final String message, final Exception cause) {
        super(message, cause);
    }
}