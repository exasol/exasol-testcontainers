package com.exasol.clusterlogs;

interface LogEntryPatternVerifier {

    /**
     * Check if the given {@link String} contains the expected log entries.
     *
     * @param stdout the {@link String} to check, may contain multiple lines.
     * @return <code>true</code> if the {@link String} contains the expected log entries, else <code>false</code>.
     */
    boolean isLogMessageFound(final String stdout);
}
