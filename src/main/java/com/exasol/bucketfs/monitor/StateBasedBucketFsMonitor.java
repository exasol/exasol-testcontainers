package com.exasol.bucketfs.monitor;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.bucketfs.ReadOnlyBucket;

// COPY
// SHOULD be replaced by dependency to bucketfs-java

/**
 * Monitor that watches the synchronization of objects in BucketFS.
 */
public interface StateBasedBucketFsMonitor {

    /**
     * Check if the object with the given path is already synchronized.
     *
     * @param bucket       bucket
     * @param pathInBucket path to the object located in the bucket
     * @param state        state in order to detect synchronization more precisely
     * @return {@code true} if the object exists in the bucket and is synchronized
     * @throws BucketAccessException if the object in the bucket is inaccessible
     */
    boolean isObjectSynchronized(final ReadOnlyBucket bucket, final String pathInBucket, final State state)
            throws BucketAccessException;

    /**
     * A {@link State} allows to detect events more precisely. For instance the state could define a point in time and
     * reject events that happened before .
     */
    public interface State {
        /**
         * @param other other state to be inspected
         * @return true if current {@link State} accepts {@link State} {@code other}.
         */
        boolean accepts(State other);

        /**
         * @return string representation of the current state for log messages
         */
        String getRepresentation();
    }

    public interface StateRetriever {
        /**
         * @return state as currently observed by the monitor
         */
        State getState();
    }
}