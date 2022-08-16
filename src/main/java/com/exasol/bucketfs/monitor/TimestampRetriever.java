package com.exasol.bucketfs.monitor;

import java.time.Instant;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;
import com.exasol.bucketfs.monitor.BucketFsMonitor.StateRetriever;

public class TimestampRetriever implements StateRetriever {
    @Override
    public State getState() {
        return TimestampState.lowResolution(Instant.now());
    }
}
