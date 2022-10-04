package org.testcontainers.containers;

/**
 * Factory class to provide a defined ExecResult for mock tests
 */
public class ExecResultFactory {
    /**
     * Static method that returns a defined ExecResult
     *
     * @param exitCode the exit code
     * @param stdout   stdout
     * @param stderr   stderr
     * @return Container.ExecResult
     */
    public static Container.ExecResult result(final int exitCode, final String stdout, final String stderr) {
        return new Container.ExecResult(exitCode, stdout, stderr);
    }
}