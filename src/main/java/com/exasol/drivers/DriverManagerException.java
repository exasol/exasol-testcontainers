package com.exasol.drivers;

public class DriverManagerException extends RuntimeException {
    private static final long serialVersionUID = -3067281612997655722L;

    public DriverManagerException(final String message, final DatabaseDriver driver, final Throwable cause) {
        this(message + " " + driver, cause);
    }

    public DriverManagerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}