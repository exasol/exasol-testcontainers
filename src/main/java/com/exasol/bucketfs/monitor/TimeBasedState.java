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
public class TimeBasedState implements StateBasedBucketFsMonitor.State { // <Instant> {

    public static TimeBasedState lowResolution(final Instant time) {
        return new TimeBasedState(time.truncatedTo(ChronoUnit.MICROS));
    }

    public static TimeBasedState of(final LocalDateTime time, final TimeZone timeZone) {
        return new TimeBasedState(time.atZone(timeZone.toZoneId()).toInstant());
    }

    private final Instant time;

    /**
     * @param earliest earliest point in time to accept other states
     */
    public TimeBasedState(final Instant time) {
        this.time = time;
    }

    @Override
    public boolean accepts(final State other) {
        if (!(other instanceof TimeBasedState)) {
            return false;
        }
        return !((TimeBasedState) other).time.isBefore(this.time);
    }

    @Override
    public String getRepresentation() {
        return "time " + this.time.toString();
    }

    public Instant getTime() {
        return this.time;
    }

}
