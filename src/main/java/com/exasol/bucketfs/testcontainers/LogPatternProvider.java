package com.exasol.bucketfs.testcontainers;

import com.exasol.bucketfs.UnsynchronizedBucket;

/**
 * Provides a pattern to identify log entries in BucketFS related to a specific path in a bucket.
 */
public interface LogPatternProvider {

    /**
     * @param pathInBucket path to an object in BucketFS
     * @return pattern to identify log entries in BucketFS related to this pathInBucket
     */
    String pattern(String pathInBucket);

    /**
     * Generates a pattern suited for Exasol databases with major version &lt; 8
     */
    public static LogPatternProvider DEFAULT = new LogPatternProvider() {
        @Override
        public String pattern(final String pathInBucket) {
            return pathInBucket + ".*" + (isSupportedArchiveFormat(pathInBucket) ? "extracted" : "linked");
        }

        private boolean isSupportedArchiveFormat(final String pathInBucket) {
            for (final String extension : UnsynchronizedBucket.SUPPORTED_ARCHIVE_EXTENSIONS) {
                if (pathInBucket.endsWith(extension)) {
                    return true;
                }
            }
            return false;
        }
    };

    // sample log messages:
    // [I 220812 11:26:06 bucketfsd:228] rsync for id (('bfsdefault', 'default', 'dir1/file.txt')) is done
    // [I 220812 11:10:21 bucketfsd:228] rsync for id (('bfsdefault', 'default', 'dir4/file.txt')) is done
    // [I 220812 10:57:23 bucketfsd:228] rsync for id (('bfsdefault', 'default', 'dir5/sub5/file.txt')) is done
    /**
     * Generates a pattern suited for Exasol databases with major version 8.
     */
    public static LogPatternProvider VERSION_8 = pathInBucket -> "rsync for .*'" //
            + (pathInBucket.startsWith("/") ? pathInBucket.substring(1) : pathInBucket) //
            + ".*'.* is done";
}
