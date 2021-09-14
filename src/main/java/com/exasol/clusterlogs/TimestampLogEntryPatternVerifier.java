package com.exasol.clusterlogs;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link LogEntryPatternVerifier} verifies that log entries appear after the given timestamp.
 */
class TimestampLogEntryPatternVerifier implements LogEntryPatternVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimestampLogEntryPatternVerifier.class);
    private static final Pattern LOG_ENTRY_PATTERN = Pattern
            .compile("\\[. (\\d\\d)(\\d\\d)(\\d\\d) (\\d\\d:\\d\\d:\\d\\d).*");

    private final Instant afterUtc;
    private final TimeZone timeZone;

    /**
     * Create a new instance of the {@link TimestampLogEntryPatternVerifier}.
     *
     * @param timeZone time zone that serves as context for the short timestamps in the logs
     * @param afterUtc earliest time in the log after which the log message must appear
     */
    TimestampLogEntryPatternVerifier(final Instant afterUtc, final TimeZone timeZone) {
        this.afterUtc = afterUtc;
        this.timeZone = timeZone;
    }

    @Override
    public boolean isLogMessageFound(final String stdout) {
        final LocalDateTime afterLocal = convertUtcToLowResulionLocal(this.afterUtc);
        try (final BufferedReader reader = new BufferedReader(new StringReader(stdout))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                final Matcher matcher = LOG_ENTRY_PATTERN.matcher(line);
                if (matcher.matches()) {
                    final String isoTimestamp = "20" + matcher.group(1) + "-" + matcher.group(2) + "-"
                            + matcher.group(3) + "T" + matcher.group(4);
                    final LocalDateTime timestamp = LocalDateTime.parse(isoTimestamp);
                    if (timestamp.isAfter(afterLocal) || timestamp.isEqual(afterLocal)) {
                        LOGGER.debug("Found matching log entry {} (after {}): {}", timestamp, afterLocal, line);
                        return true;
                    }
                }
            }
            return false;
        } catch (final IOException e) {
            throw new UncheckedIOException("Error reading string", e);
        }
    }

    private LocalDateTime convertUtcToLowResulionLocal(final Instant afterUTC) {
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(afterUTC, this.timeZone.toZoneId());
        return localDateTime.withNano(0);
    }

    @Override
    public String toString() {
        return "TimestampLogEntryPatternVerifier [afterUtc=" + this.afterUtc + ", timeZone=" + this.timeZone + "]";
    }
}
