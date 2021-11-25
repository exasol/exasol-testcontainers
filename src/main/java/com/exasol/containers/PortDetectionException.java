package com.exasol.containers;

/**
 * Exception for failed detection of service ports.
 */
public class PortDetectionException extends UnsupportedOperationException {
    private static final long serialVersionUID = -1871794026177194823L;

    /**
     * Create a new instance of a {@link PortDetectionException}.
     *
     * @param service service for which the port could not be detected.
     */
    public PortDetectionException(final String service) {
        super("Could not detect internal " + service + " port for custom image. "
                + "Please specify the port explicitly using withExposedPorts().");
    }
}