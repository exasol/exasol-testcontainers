package com.exasol.drivers;

/**
 * Exception class for problems while managing drivers for external data repositories.
 */
public class DriverManagerException extends RuntimeException {
    private static final long serialVersionUID = -3067281612997655722L;
    /** @serial */
    private final DatabaseDriver driver;

    /**
     * Create a exception in the context of a database driver.
     *
     * @param message general error message
     * @param driver  driver that was the context
     * @param cause   error causing this exception
     */
    public DriverManagerException(final String message, final DatabaseDriver driver, final Throwable cause) {
        super(message + (driver == null ? "" : " " + driver), cause);
        this.driver = driver;
    }

    /**
     * Crate an exception that is driver-independent.
     *
     * @param message error message
     * @param cause   error causing this exception
     */
    public DriverManagerException(final String message, final Throwable cause) {
        this(message, null, cause);
    }

    /**
     * Get the driver associated with this exception.
     *
     * @return driver
     */
    public DatabaseDriver getDriver() {
        return this.driver;
    }

    /**
     * Check whether a driver is associated with this exception.
     *
     * @return {@code true} if a driver is associated with this exception.
     */
    public boolean hasDriver() {
        return this.driver != null;
    }
}