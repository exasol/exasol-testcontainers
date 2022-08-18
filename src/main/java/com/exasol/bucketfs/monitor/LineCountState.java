package com.exasol.bucketfs.monitor;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;

/**
 * Only accept other states with higher line number.
 */
public class LineCountState implements BucketFsMonitor.State {

    private final long lineNumber;

    /**
     * @param line current line number in log file.
     */
    public LineCountState(final long line) {
        this.lineNumber = line;
    }

    @Override
    public boolean accepts(final State other) {
        if (!(other instanceof LineCountState)) {
            return false;
        }
        return ((LineCountState) other).lineNumber > this.lineNumber;
    }

    @Override
    public String toString() {
        return "line number " + this.lineNumber;
    }

    /**
     * @return line number representing the current state
     */
    public long getLineNumber() {
        return this.lineNumber;
    }
}
