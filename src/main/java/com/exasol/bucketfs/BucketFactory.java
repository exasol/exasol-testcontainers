package com.exasol.bucketfs;

import java.util.HashMap;
import java.util.Map;

import com.exasol.config.*;

/**
 * Factor for objects abstracting buckets in Exasol's BucketFS.
 */
public final class BucketFactory {
    private final Map<String, Bucket> buckets = new HashMap<>();
    private final String ipAddress;
    private final ClusterConfiguration clusterConfiguration;
    private final Map<Integer, Integer> portMappings;

    /**
     * Create a new instance of a BucketManager.
     *
     * @param ipAddress            IP address of the the BucketFS service
     * @param clusterConfiguration configuration of the Exasol Cluster
     * @param portMappings         mapping of container internal to exposed port numbers
     */
    public BucketFactory(final String ipAddress, final ClusterConfiguration clusterConfiguration,
            final Map<Integer, Integer> portMappings) {
        this.ipAddress = ipAddress;
        this.clusterConfiguration = clusterConfiguration;
        this.portMappings = portMappings;
    }

    private int mapPort(final int internalPort) {
        return this.portMappings.get(internalPort);
    }

    /**
     * Get a BucketFS bucket.
     *
     * @param configuration bucket configuration
     * @return bucket
     */
    public synchronized Bucket getBucket(final String serviceName, final String bucketName) {
        final BucketFsServiceConfiguration serviceConfiguration = this.clusterConfiguration
                .getBucketFsServiceConfiguration(serviceName);
        final BucketConfiguration bucketConfiguration = serviceConfiguration.getBucketConfiguration(bucketName);
        final String bucketPath = serviceName + BucketConstants.PATH_SEPARATOR + bucketName;
        if (!this.buckets.containsKey(bucketPath)) {
            final Bucket bucket = Bucket //
                    .builder() //
                    .serviceName(serviceName) //
                    .name(bucketName) //
                    .ipAddress(this.ipAddress) //
                    .httpPort(mapPort(serviceConfiguration.getHttpPort())) //
                    .readPassword(bucketConfiguration.getReadPassword()) //
                    .writePassword(bucketConfiguration.getWritePassword()) //
                    .build();
            this.buckets.put(bucketPath, bucket);
        }
        return this.buckets.get(bucketPath);
    }
}