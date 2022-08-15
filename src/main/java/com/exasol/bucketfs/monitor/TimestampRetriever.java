package com.exasol.bucketfs.monitor;

import java.time.Instant;

import com.exasol.bucketfs.monitor.StateBasedBucketFsMonitor.State;
import com.exasol.bucketfs.monitor.StateBasedBucketFsMonitor.StateRetriever;

public class TimestampRetriever implements StateRetriever {
    @Override
    public State getState() {
        return TimestampState.lowResolution(Instant.now());
    }
}
