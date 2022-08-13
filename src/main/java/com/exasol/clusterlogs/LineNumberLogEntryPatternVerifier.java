package com.exasol.clusterlogs;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LineNumberLogEntryPatternVerifier implements LogEntryPatternVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(LineNumberLogEntryPatternVerifier.class);
    private static final Pattern LOG_ENTRY_PATTERN = Pattern.compile("(\\d+) .*");

    private final long afterLine;

    /**
     * @param afterLine current length of log file. Pattern is only accepted in later lines.
     */
    LineNumberLogEntryPatternVerifier(final long afterLine) {
        this.afterLine = afterLine;
    }

    @Override
    public boolean isLogMessageFound(final String stdout) {
        try (final BufferedReader reader = new BufferedReader(new StringReader(stdout))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                final Matcher matcher = LOG_ENTRY_PATTERN.matcher(line);
                if (matcher.matches()) {
                    final long n = Long.parseLong(matcher.group(1));
                    if (n > this.afterLine) {
                        LOGGER.debug("Found matching log entry in line {} (> {}): {}", n, this.afterLine, line);
                        return true;
                    }
                }
            }
            return false;
        } catch (final IOException exception) {
            throw new UncheckedIOException("Error reading string", exception);
        }
    }

    @Override
    public String toString() {
        return String.format("%s [afterLine=%s]", //
                this.getClass().getSimpleName(), this.afterLine);
    }

}
