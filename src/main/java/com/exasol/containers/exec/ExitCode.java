package com.exasol.containers.exec;

/**
 * Exit codes for in-container command execution.
 */
public final class ExitCode {
    /** Exit code to signal successful completion */
    public static final int OK = 0;

    private ExitCode() {
        // prevent instantiation.
    }
}