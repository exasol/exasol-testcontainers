package com.exasol.bucketfs.monitor;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;

/**
 * Only accept other states with higher line number.
 */
public class FilesizeState implements BucketFsMonitor.State {

    private final Long lineNumber;

    /**
     * @param line current line number in log file.
     */
    public FilesizeState(final Long line) {
        this.lineNumber = line;
    }

    @Override
    public boolean accepts(final State other) {
        if (!(other instanceof FilesizeState)) {
            return false;
        }
        return ((FilesizeState) other).lineNumber > this.lineNumber;
    }

    @Override
    public String toString() {
        return "line number " + this.lineNumber;
    }

    public long getLineNumber() {
        return this.lineNumber;
    }
}
