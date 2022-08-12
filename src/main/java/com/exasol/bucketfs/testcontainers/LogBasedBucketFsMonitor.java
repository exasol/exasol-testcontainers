package com.exasol.bucketfs.testcontainers;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;

import java.io.IOException;
import java.time.Instant;

import com.exasol.bucketfs.*;
import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;

/**
 * This {@link BucketFsMonitor} detects if a file was successfully uploaded from the Exasol log files.
 */
public class LogBasedBucketFsMonitor implements BucketFsMonitor {
    private final LogPatternDetectorFactory detectorFactory;

    /**
     * Create a new instance of {@link LogBasedBucketFsMonitor}.
     *
     * @param detectorFactory factory for a log pattern detector
     */
    public LogBasedBucketFsMonitor(final LogPatternDetectorFactory detectorFactory) {
        this.detectorFactory = detectorFactory;
    }

    private static boolean isSupportedArchiveFormat(final String pathInBucket) {
        for (final String extension : UnsynchronizedBucket.SUPPORTED_ARCHIVE_EXTENSIONS) {
            if (pathInBucket.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("java:S112")
    public boolean isObjectSynchronized(final ReadOnlyBucket bucket, final String pathInBucket, final Instant afterUTC)
            throws BucketAccessException {
        try {
//            return createBucketLogPatternDetector(pathInBucket).isPatternPresent();
            return createBucketLogPatternDetector(pathInBucket, afterUTC).isPatternPresent();
        } catch (final IOException exception) {
            throw new BucketAccessException(
                    "Unable to check if object \"" + pathInBucket + "\" is synchronized in bucket \""
                            + bucket.getBucketFsName() + "/" + bucket.getBucketName() + "\".",
                    exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                    "Caught interrupt trying to check if object \"" + pathInBucket + "\" is synchronized in bucket \""
                            + bucket.getBucketFsName() + "/" + bucket.getBucketName() + "\".",
                    exception);
        }
    }

    private LogPatternDetector createBucketLogPatternDetector(final String pathInBucket, final long afterLineNumber) {
        return this.detectorFactory.lineCountingDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, pattern(pathInBucket), afterLineNumber);
    }

    // sample log messages:
    // [I 220812 10:57:23 bucketfsd:228] removed sync future for id (('bfsdefault', 'default', 'dir5/sub5/file.txt'))
    // [I 220812 11:10:21 bucketfsd:228] removed sync future for id (('bfsdefault', 'default', 'dir4/file.txt'))
    private String pattern(final String pathInBucket) {
        return "removed sync future for id .*'" //
                + (pathInBucket.startsWith("/") ? pathInBucket.substring(1) : pathInBucket) //
                + ".*'";
    }

    private LogPatternDetector createBucketLogPatternDetector(final String pathInBucket, final Instant afterUTC) {
//        final String pattern = pathInBucket + ".*" + (isSupportedArchiveFormat(pathInBucket) ? "extracted" : "linked");
        return this.detectorFactory.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, pattern(pathInBucket), afterUTC);
    }
}
