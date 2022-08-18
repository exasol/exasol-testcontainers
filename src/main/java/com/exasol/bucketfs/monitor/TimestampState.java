package com.exasol.bucketfs.monitor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;

/**
 * Reject other states with earlier time.
 */
public class TimestampState implements BucketFsMonitor.State {

    /**
     * Create a new instance of {@link TimestampState}
     *
     * @param time current instant in time before the operation waiting for synchronization.
     * @return state representing time truncated to low resolution by discarding fractions of seconds.
     */
    public static TimestampState lowResolution(final Instant time) {
        return new TimestampState(time.truncatedTo(ChronoUnit.SECONDS));
    }

    /**
     * Create a new instance of {@link TimestampState}
     *
     * @param time     current instant in time represented as {@link LocalDateTime}
     * @param timeZone time zone
     * @return time stamp state representing this time
     */
    public static TimestampState of(final LocalDateTime time, final TimeZone timeZone) {
        return new TimestampState(time.atZone(timeZone.toZoneId()).toInstant());
    }

    private final Instant time;

    /**
     * @param time earliest point in time to accept other states
     */
    TimestampState(final Instant time) {
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
    public String toString() {
        return "time " + this.time.toString();
    }

    /**
     * @return instant of time defining the current state.
     */
    public Instant getTime() {
        return this.time;
    }

}
