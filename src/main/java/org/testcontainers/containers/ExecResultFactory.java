package org.testcontainers.containers;

import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.ExasolContainer;

/**
 * As return type {@link ExecResult} of inherited methods in {@link ExasolContainer} is package-private we need this
 * factory class in the same package to override these methods. Also used by tests.
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

    private ExecResultFactory() {
        // only static usage
    }
}