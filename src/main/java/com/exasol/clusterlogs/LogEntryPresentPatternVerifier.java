package com.exasol.clusterlogs;

class LogEntryPresentPatternVerifier implements LogEntryPatternVerifier {

    @Override
    public boolean isLogMessageFound(final String stdout) {
        return !stdout.isBlank();
    }
}
