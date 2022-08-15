package com.exasol.bucketfs.testcontainers;

import java.util.HashMap;
import java.util.Map;

import com.exasol.bucketfs.*;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor.FilterStrategy;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.*;

/**
 * Factory for objects abstracting buckets in Exasol's BucketFS.
 */
public final class TestcontainerBucketFactory implements BucketFactory {

    public static Builder builder() {
        return new Builder();
    }

    private final Map<String, Bucket> buckets = new HashMap<>();
    private String host;
    private ClusterConfiguration clusterConfiguration;
    private Map<Integer, Integer> portMappings;
    private LogPatternDetectorFactory detectorFactory;
    private FilterStrategy filterStrategy = FilterStrategy.TIME_STAMP;

    private TestcontainerBucketFactory() {
        // use builder!
    }

    /**
     * Get a BucketFS bucket.
     *
     * @param serviceName name of the BucketFS service that hosts the bucket
     * @param bucketName  name of the bucket
     * @return bucket
     */
    // [impl->dsn~bucket-api~1]
    @Override
    public synchronized Bucket getBucket(final String serviceName, final String bucketName) {
        final BucketFsServiceConfiguration serviceConfiguration = this.clusterConfiguration
                .getBucketFsServiceConfiguration(serviceName);
        final BucketConfiguration bucketConfiguration = serviceConfiguration.getBucketConfiguration(bucketName);
        final String bucketPath = serviceName + BucketConstants.PATH_SEPARATOR + bucketName;
        this.buckets.computeIfAbsent(bucketPath, key -> {
            final LogBasedBucketFsMonitor monitor = new LogBasedBucketFsMonitor( //
                    this.detectorFactory, this.filterStrategy);
            return SyncAwareBucket2 //
                    .builder() //
                    .monitor(monitor)//
                    .stateRetriever(monitor.createStateRetriever()) //
                    .serviceName(serviceName) //
                    .name(bucketName) //
                    .ipAddress(this.host) //
                    .port(mapPort(serviceConfiguration.getHttpPort())) //
                    .readPassword(bucketConfiguration.getReadPassword()) //
                    .writePassword(bucketConfiguration.getWritePassword()) //
                    .build();
        });
        return this.buckets.get(bucketPath);
    }

    private int mapPort(final int internalPort) {
        return this.portMappings.get(internalPort);
    }

    public static class Builder {
        private final TestcontainerBucketFactory factory = new TestcontainerBucketFactory();

        /**
         * @param value host name or IP address of the the BucketFS service
         * @return this for fluent programming
         */
        public Builder host(final String value) {
            this.factory.host = value;
            return this;
        }

        /**
         * @param value configuration of the Exasol Cluster
         * @return this for fluent programming
         */
        public Builder clusterConfiguration(final ClusterConfiguration value) {
            this.factory.clusterConfiguration = value;
            return this;
        }

        /**
         * @param value mapping of container internal to exposed port numbers
         * @return this for fluent programming
         */
        public Builder portMappings(final Map<Integer, Integer> value) {
            this.factory.portMappings = value;
            return this;
        }

        /**
         * @param value log entry pattern detector factory
         * @return this for fluent programming
         */
        public Builder detectorFactory(final LogPatternDetectorFactory value) {
            this.factory.detectorFactory = value;
            return this;
        }

        /**
         * @param value filter strategy to be used to reject irrelevant log entries
         * @return this for fluent programming
         */
        public Builder filterStrategy(final FilterStrategy value) {
            this.factory.filterStrategy = value;
            return this;
        }

        public TestcontainerBucketFactory build() {
            return this.factory;
        }
    }
}
