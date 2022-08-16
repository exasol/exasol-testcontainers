package com.exasol.bucketfs.testcontainers;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;
import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.IOException;

import com.exasol.bucketfs.*;
import com.exasol.bucketfs.monitor.BucketFsMonitor;
import com.exasol.bucketfs.monitor.TimestampRetriever;
import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.containers.ExasolDockerImageReference;

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
        } catch (final IOException exception) {
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
        return this.detectorFactory.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, pattern(pathInBucket), state);
    }

    public StateRetriever createStateRetriever() {
        switch (this.filterStrategy) {
        case LINE_NUMBER:
            return this.detectorFactory.createFileSizeRetriever( //
                    EXASOL_CORE_DAEMON_LOGS_PATH, //
                    BUCKETFS_DAEMON_LOG_FILENAME_PATTERN);
        case TIME_STAMP:
        default:
            return new TimestampRetriever();
        }
    }

    // sample log messages:
    // [I 220812 11:26:06 bucketfsd:228] rsync for id (('bfsdefault', 'default', 'dir1/file.txt')) is done
    // [I 220812 11:10:21 bucketfsd:228] rsync for id (('bfsdefault', 'default', 'dir4/file.txt')) is done
    // [I 220812 10:57:23 bucketfsd:228] rsync for id (('bfsdefault', 'default', 'dir5/sub5/file.txt')) is done
    private String pattern(final String pathInBucket) {
        if (isOldVersion()) {
            return pathInBucket + ".*" + (isSupportedArchiveFormat(pathInBucket) ? "extracted" : "linked");
        } else {
            return "rsync for .*'" //
                    + (pathInBucket.startsWith("/") ? pathInBucket.substring(1) : pathInBucket) //
                    + ".*'.* is done";
        }
    }

    private boolean isOldVersion() {
        final ExasolDockerImageReference dockerImageReference = this.detectorFactory.getDockerImageReference();
        return (dockerImageReference.hasMajor() && (dockerImageReference.getMajor() < 8));
    }

    private static boolean isSupportedArchiveFormat(final String pathInBucket) {
        for (final String extension : UnsynchronizedBucket.SUPPORTED_ARCHIVE_EXTENSIONS) {
            if (pathInBucket.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * To identify relevant log entries the log monitor can
     * <ul>
     * <li>either use the current time and accept only newer log entries</li>
     * <li>of use the current size of the log file in terms of the number of lines and accept only log entries with
     * higher line number. For this strategy it is mandatory to disable log rotation.</li>
     * </ul>
     */
    public enum FilterStrategy {
        TIME_STAMP, //
        LINE_NUMBER;
    }
}
