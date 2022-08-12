package com.exasol.clusterlogs;

/**
 * Strategy used by {@link LogPatternDetector} for checking if given log messages match the expectation, e.g. if log
 * timestamps are within expected range.
 */
interface LogEntryPatternVerifier {

    public static final LogEntryPatternVerifier ALWAYS_TRUE = text -> true;

    /**
     * Check if the given {@link String} contains the expected log entries.
     *
     * @param text {@link String} to check, may contain multiple lines.
     * @return <code>true</code> if the {@link String} contains the expected log entries, else <code>false</code>.
     */
    boolean isLogMessageFound(final String text);
}
