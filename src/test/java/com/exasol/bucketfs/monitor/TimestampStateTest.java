package com.exasol.bucketfs.monitor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.*;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;

class TimestampStateTest {

    private static final String STRING = "2022-05-20T09:20:44Z";
    private static final Instant INSTANT = Instant.parse(STRING);
    private static final TimestampState TESTEE = TimestampState.lowResolution(INSTANT.plusMillis(200));
    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");
    private static final TimeZone TIMEZONE = TimeZone.getTimeZone(TimestampStateTest.ZONE_ID);

    @Test
    void lowResolution() {
        assertThat(TESTEE.getTime(), equalTo(INSTANT));
        assertThat(TESTEE.toString(), equalTo("time " + STRING));
        assertThat(TESTEE.accepts(TimestampState.lowResolution(INSTANT)), is(true));
    }

    @Test
    void localDateTime() {
        final LocalDateTime local = LocalDateTime.ofInstant(INSTANT, ZONE_ID);
        final TimestampState actual = TimestampState.of(local, TIMEZONE);
        assertThat(actual.getTime(), equalTo(INSTANT));
    }

    @Test
    void accepts() {
        final Duration delta = Duration.ofSeconds(1);
        assertThat(TESTEE.accepts(TESTEE), is(true));
        assertThat(TESTEE.accepts(before(delta)), is(false));
        assertThat(TESTEE.accepts(after(delta)), is(true));
    }

    private State before(final Duration delta) {
        return localTimestampState(INSTANT.minus(delta));
    }

    private State after(final Duration delta) {
        return localTimestampState(INSTANT.plus(delta));
    }

    private TimestampState localTimestampState(final Instant instant) {
        return TimestampState.of(LocalDateTime.ofInstant(instant, ZONE_ID), TIMEZONE);
    }

}
