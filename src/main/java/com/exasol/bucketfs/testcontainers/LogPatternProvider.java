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

    /**
     * Generates a pattern suited for Exasol databases with major version 8.
     * <p>
     * See {@code LogPatternProviderTest} for examples.
     */
    public static LogPatternProvider VERSION_8 = pathInBucket -> {
        final String path = pathInBucket.startsWith("/") ? pathInBucket.substring(1) : pathInBucket;
        return "rsync for .*'" + path + ".*'.* is done";
    };
}
