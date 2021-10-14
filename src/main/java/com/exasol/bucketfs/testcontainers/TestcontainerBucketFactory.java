package com.exasol.bucketfs.testcontainers;

import java.util.HashMap;
import java.util.Map;

import com.exasol.bucketfs.*;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.*;

/**
 * Factory for objects abstracting buckets in Exasol's BucketFS.
 */
public final class TestcontainerBucketFactory implements BucketFactory {
    private final Map<String, Bucket> buckets = new HashMap<>();
    private final String ipAddress;
    private final ClusterConfiguration clusterConfiguration;
    private final Map<Integer, Integer> portMappings;
    private final LogPatternDetectorFactory detectorFactory;

    /**
     * Create a new instance of a TestcontainerBucketFactory.
     *
     * @param detectorFactory      log entry pattern detector factory
     * @param ipAddress            IP address of the the BucketFS service
     * @param clusterConfiguration configuration of the Exasol Cluster
     * @param portMappings         mapping of container internal to exposed port numbers
     */
    public TestcontainerBucketFactory(final LogPatternDetectorFactory detectorFactory, final String ipAddress,
            final ClusterConfiguration clusterConfiguration, final Map<Integer, Integer> portMappings) {
        this.ipAddress = ipAddress;
        this.clusterConfiguration = clusterConfiguration;
        this.portMappings = portMappings;
        this.detectorFactory = detectorFactory;
    }

    private int mapPort(final int internalPort) {
        return this.portMappings.get(internalPort);
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
        this.buckets.computeIfAbsent(bucketPath, key -> SyncAwareBucket //
                .builder() //
                .monitor(new LogBasedBucketFsMonitor(this.detectorFactory))//
                .serviceName(serviceName) //
                .name(bucketName) //
                .ipAddress(this.ipAddress) //
                .port(mapPort(serviceConfiguration.getHttpPort())) //
                .readPassword(bucketConfiguration.getReadPassword()) //
                .writePassword(bucketConfiguration.getWritePassword()) //
                .build());
        return this.buckets.get(bucketPath);
    }
}
