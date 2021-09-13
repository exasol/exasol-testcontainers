package com.exasol.clusterlogs;

/**
 * This {@link LogEntryPatternVerifier} only ensures that log entries are found, i.e. the log is not empty.
 */
class LogEntryPresentPatternVerifier implements LogEntryPatternVerifier {

    @Override
    public boolean isLogMessageFound(final String text) {
        return !text.isBlank();
    }
}
