package org.testcontainers.containers;

import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.ExasolContainer;

/**
 * {@link ExecResultFactory} is required to enable {@link ExasolContainer} override methods of extended class
 * {@link TestContainer} as the return type {@link ExecResult} of some of the overridden methods is package-private to
 * {@link org.testcontainers.containers}. Also used by tests.
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