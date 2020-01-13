package com.exasol.bucketfs;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.Container;

import com.exasol.config.*;

/**
 * Factory for objects abstracting buckets in Exasol's BucketFS.
 */
public final class BucketFactory {
    private final Map<String, Bucket> buckets = new HashMap<>();
    private final String ipAddress;
    private final ClusterConfiguration clusterConfiguration;
    private final Map<Integer, Integer> portMappings;
    private final Container<? extends Container<?>> container;

    /**
     * Create a new instance of a BucketFactory.
     *
     * @param container            parent container
     * @param ipAddress            IP address of the the BucketFS service
     * @param clusterConfiguration configuration of the Exasol Cluster
     * @param portMappings         mapping of container internal to exposed port numbers
     */
    public BucketFactory(final Container<? extends Container<?>> container, final String ipAddress,
            final ClusterConfiguration clusterConfiguration, final Map<Integer, Integer> portMappings) {
        this.container = container;
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
     * @param serviceName name of the BucketFS service that hosts the bucket
     * @param bucketName  name of the bucket
     * @return bucket
     */
    // [impl->dsn~bucket-factory-injects-access-credentials~1]
    public synchronized Bucket getBucket(final String serviceName, final String bucketName) {
        final BucketFsServiceConfiguration serviceConfiguration = this.clusterConfiguration
                .getBucketFsServiceConfiguration(serviceName);
        final BucketConfiguration bucketConfiguration = serviceConfiguration.getBucketConfiguration(bucketName);
        final String bucketPath = serviceName + BucketConstants.PATH_SEPARATOR + bucketName;
        if (!this.buckets.containsKey(bucketPath)) {
            final Bucket bucket = Bucket //
                    .builder() //
                    .container(this.container) //
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
