package org.testcontainers.containers;

/**
 * Factory class to provide a defined ExecResult for mock tests
 */
public class ExecResultFactory {
    public static Container.ExecResult mockResult(final int exitCode, final String stdout, final String stderr) {
        return new Container.ExecResult(exitCode, stdout, stderr);
    }
}