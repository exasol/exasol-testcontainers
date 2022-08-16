package com.exasol.bucketfs.monitor;

import java.time.Instant;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;
import com.exasol.bucketfs.monitor.BucketFsMonitor.StateRetriever;

/**
 * Retrieves the initial {@link TimestampState}, i.e. the current instant in time. This allows rejecting events that
 * happened at an earlier point in time.
 */
public class TimestampRetriever implements StateRetriever {
    @Override
    public State getState() {
        return TimestampState.lowResolution(Instant.now());
    }
}
