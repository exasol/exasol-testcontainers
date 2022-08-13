package com.exasol.bucketfs.monitor;

import com.exasol.bucketfs.monitor.StateBasedBucketFsMonitor.State;

// COPY
// SHOULD be replaced by dependency to bucketfs-java
// but in the long term might be moved to exasol-testcontainers

/**
 * Only accept other states with higher line number.
 */
public class LineNumberBasedState implements StateBasedBucketFsMonitor.State {

    private final Long lineNumber;

    /**
     * @param line current line number in log file.
     */
    public LineNumberBasedState(final Long line) {
        this.lineNumber = line;
    }

    @Override
    public boolean accepts(final State other) {
        if (!(other instanceof LineNumberBasedState)) {
            return false;
        }
        return ((LineNumberBasedState) other).lineNumber > this.lineNumber;
    }

    @Override
    public String getRepresentation() {
        return "line number " + this.lineNumber;
    }

    public long getLineNumber() {
        return this.lineNumber;
    }
}
