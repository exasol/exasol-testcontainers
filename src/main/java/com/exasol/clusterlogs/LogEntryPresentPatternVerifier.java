package com.exasol.clusterlogs;

/**
 * This {@class LogEntryPresentPatternVerifier} only ensures that log entries are found, i.e. the log is not empty.
 */
class LogEntryPresentPatternVerifier implements LogEntryPatternVerifier {

    @Override
    public boolean isLogMessageFound(final String text) {
        return !text.isBlank();
    }

    @Override
    public String toString() {
        return "LogEntryPresentPatternVerifier []";
    }
}
