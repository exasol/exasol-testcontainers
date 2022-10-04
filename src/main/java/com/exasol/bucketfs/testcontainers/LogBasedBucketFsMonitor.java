package com.exasol.bucketfs.testcontainers;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;
import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.IOException;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.bucketfs.ReadOnlyBucket;
import com.exasol.bucketfs.monitor.BucketFsMonitor;
import com.exasol.bucketfs.monitor.TimestampRetriever;
import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.containers.ssh.SshException;

/**
 * This {@link BucketFsMonitor} detects if a file was successfully uploaded from the Exasol log files.
 */
public class LogBasedBucketFsMonitor implements BucketFsMonitor {

    private final LogPatternDetectorFactory detectorFactory;
    private final FilterStrategy filterStrategy;

    /**
     * Create a new instance of {@link LogBasedBucketFsMonitor}.
     *
     * @param detectorFactory factory for a log pattern detector
     * @param filterStrategy  {@link FilterStrategy} for finding relevant log entries
     */
    public LogBasedBucketFsMonitor(final LogPatternDetectorFactory detectorFactory,
            final FilterStrategy filterStrategy) {
        this.detectorFactory = detectorFactory;
        this.filterStrategy = filterStrategy;
    }

    @Override
    @SuppressWarnings("java:S112")
    public boolean isObjectSynchronized(final ReadOnlyBucket bucket, final String pathInBucket, final State state)
            throws BucketAccessException {
        try {
            return createBucketLogPatternDetector(pathInBucket, state).isPatternPresent();
        } catch (final IOException | SshException exception) {
            throw new BucketAccessException(messageBuilder("E-ETC-19").message( //
                    "Unable to check if object {{path}} is synchronized in bucket {{bucket filesystem}}/{{bucket name}}.", //
                    pathInBucket, bucket.getBucketFsName(), bucket.getBucketName()) //
                    .toString(), //
                    exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(messageBuilder("E-ETC-20").message( //
                    "Caught interrupt trying to check if object {{path}} is synchronized in bucket {{bucket filesystem}}/{{bucket name}}.", //
                    pathInBucket, bucket.getBucketFsName(), bucket.getBucketName()) //
                    .toString(), //
                    exception);
        }
    }

    private LogPatternDetector createBucketLogPatternDetector(final String pathInBucket,
            final BucketFsMonitor.State state) {
        return this.detectorFactory.createLogPatternDetector( //
                EXASOL_CORE_DAEMON_LOGS_PATH, //
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, //
                this.detectorFactory.getLogPatternProvider().pattern(pathInBucket), //
                state);
    }

    /**
     * @return {@link StateRetriever} using the filter strategy of this {@link LogBasedBucketFsMonitor}.
     */
    public StateRetriever createStateRetriever() {
        switch (this.filterStrategy) {
        case LINE_NUMBER:
            return this.detectorFactory.createLineCountRetriever( //
                    EXASOL_CORE_DAEMON_LOGS_PATH, //
                    BUCKETFS_DAEMON_LOG_FILENAME_PATTERN);
        case TIME_STAMP:
        default:
            return new TimestampRetriever();
        }
    }

    /**
     * To identify relevant log entries the log monitor can
     * <ul>
     * <li>either use the current time and accept only newer log entries</li>
     * <li>or use the current line count of the log file and accept only log entries with higher line count. For this
     * strategy it is mandatory to disable log rotation.</li>
     * </ul>
     */
    public enum FilterStrategy {
        /** time stamp {@link FilterStrategy} */
        TIME_STAMP, //
        /** line number {@link FilterStrategy} */
        LINE_NUMBER;
    }
}
