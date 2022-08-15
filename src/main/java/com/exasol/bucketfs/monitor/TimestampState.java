package com.exasol.bucketfs.monitor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import com.exasol.bucketfs.monitor.StateBasedBucketFsMonitor.State;

// COPY
// SHOULD be replaced by dependency to bucketfs-java
// but in the long term might be moved to exasol-testcontainers

/**
 * Reject other states with earlier time.
 */
public class TimestampState implements StateBasedBucketFsMonitor.State { // <Instant> {

    public static TimestampState lowResolution(final Instant time) {
        return new TimestampState(time.truncatedTo(ChronoUnit.MICROS));
    }

    public static TimestampState of(final LocalDateTime time, final TimeZone timeZone) {
        return new TimestampState(time.atZone(timeZone.toZoneId()).toInstant());
    }

    private final Instant time;

    /**
     * @param earliest earliest point in time to accept other states
     */
    public TimestampState(final Instant time) {
        this.time = time;
    }

    @Override
    public boolean accepts(final State other) {
        if (!(other instanceof TimestampState)) {
            return false;
        }
        return !((TimestampState) other).time.isBefore(this.time);
    }

    @Override
    public String getRepresentation() {
        return "time " + this.time.toString();
    }

    public Instant getTime() {
        return this.time;
    }

}
