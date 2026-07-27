package com.exasol.bucketfs.testcontainers;

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
     * Generates a pattern suited for Exasol databases with major version 8 and later.
     * <p>
     * See {@code LogPatternProviderTest} for examples.
     */
    public static LogPatternProvider DEFAULT = pathInBucket -> {
        final String path = pathInBucket.startsWith("/") ? pathInBucket.substring(1) : pathInBucket;
        return "rsync for .*'" + path + ".*'.* is done";
    };
}
