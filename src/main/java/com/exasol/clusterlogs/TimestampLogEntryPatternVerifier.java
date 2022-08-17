package com.exasol.clusterlogs;

import java.io.*;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.bucketfs.monitor.BucketFsMonitor;
import com.exasol.bucketfs.monitor.TimestampState;

/**
 * This {@class TimestampLogEntryPatternVerifier} verifies that log entries appear after the given timestamp.
 */
class TimestampLogEntryPatternVerifier implements LogEntryPatternVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimestampLogEntryPatternVerifier.class);
    private static final Pattern LOG_ENTRY_PATTERN = Pattern
            .compile("\\[. (\\d\\d)(\\d\\d)(\\d\\d) (\\d\\d:\\d\\d:\\d\\d).*");

    private final BucketFsMonitor.State state;
    private final TimeZone timeZone;

    /**
     * Create a new instance of the {@link TimestampLogEntryPatternVerifier}.
     *
     * @param timeZone time zone that serves as context for the short timestamps in the logs
     * @param afterUtc earliest time in the log after which the log message must appear
     */
    TimestampLogEntryPatternVerifier(final BucketFsMonitor.State state, final TimeZone timeZone) {
        this.state = state;
        this.timeZone = timeZone;
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
                    final String isoTimestamp = "20" + matcher.group(1) + "-" + matcher.group(2) + "-"
                            + matcher.group(3) + "T" + matcher.group(4);
                    final TimestampState logEntryState = TimestampState.of(LocalDateTime.parse(isoTimestamp),
                            this.timeZone);
                    if (this.state.accepts(logEntryState)) {
                        LOGGER.debug("Found matching log entry with {} (after {}): {}", //
                                logEntryState, this.state, line);
                        return true;
                    } else {
                        LOGGER.debug("Ignoring log entry with {} (after {}): {}", //
                                logEntryState, this.state, line);
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
        return "TimestampLogEntryPatternVerifier [afterUtc=" + this.state.toString() + ", timeZone="
                + this.timeZone.getID() + "]";
    }
}
