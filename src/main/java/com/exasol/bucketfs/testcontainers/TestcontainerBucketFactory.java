package com.exasol.bucketfs.testcontainers;

import java.security.cert.X509Certificate;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.bucketfs.*;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor.FilterStrategy;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.*;

/**
 * Factory for objects abstracting buckets in Exasol's BucketFS.
 */
public final class TestcontainerBucketFactory implements BucketFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestcontainerBucketFactory.class);

    /**
     * @return Builder to create new instances of {@link TestcontainerBucketFactory}
     */
    public static Builder builder() {
        return new Builder();
    }

    private final Map<String, Bucket> buckets = new HashMap<>();
    private String host;
    private ClusterConfiguration clusterConfiguration;
    private X509Certificate certificate;
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
            final com.exasol.bucketfs.SyncAwareBucket.Builder<?> builder = SyncAwareBucket //
                    .builder() //
                    .monitor(monitor)//
                    .stateRetriever(monitor.createStateRetriever()) //
                    .serviceName(serviceName) //
                    .name(bucketName) //
                    .host(this.host) //
                    .certificate(this.certificate) //
                    .allowAlternativeHostName(this.host) //
                    .readPassword(bucketConfiguration.getReadPassword()) //
                    .writePassword(bucketConfiguration.getWritePassword());
            if (serviceConfiguration.getHttpsPort() != 0) {
                final int mappedPort = mapPort(serviceConfiguration.getHttpsPort());
                LOGGER.debug("Using encrypted BucketFS port {} (mapped to {})", serviceConfiguration.getHttpsPort(),
                        mappedPort);
                builder.port(mappedPort).useTls(true);
            } else if (serviceConfiguration.getHttpPort() != 0) {
                final int mappedPort = mapPort(serviceConfiguration.getHttpPort());
                LOGGER.debug("Using unencrypted BucketFS port {} (mapped to {})", serviceConfiguration.getHttpPort(),
                        mappedPort);
                builder.port(mappedPort).useTls(false);
            } else {
                throw new IllegalStateException("Neither HTTPS nor HTTP port is defined for BucketFS");
            }
            return builder.build();
        });
        return this.buckets.get(bucketPath);
    }

    private int mapPort(final int internalPort) {
        final Integer port = this.portMappings.get(internalPort);
        if (port == null) {
            throw new IllegalStateException(
                    "Internal BucketFS port " + internalPort + " is not mapped. Current port mappings: " + this.portMappings);
        }
        return port.intValue();
    }

    /**
     * Builder to create new instances of {@link TestcontainerBucketFactory}.
     */
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
            this.factory.portMappings = Objects.requireNonNull(value, "portMappings");
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

        /**
         * @param certificate TLS certificate for connecting to the BucketFS service
         * @return this for fluent programming
         */
        public Builder certificate(final X509Certificate certificate) {
            this.factory.certificate = Objects.requireNonNull(certificate, "certificate");
            return this;
        }

        /**
         * @return new instance of {@link TestcontainerBucketFactory}
         */
        public TestcontainerBucketFactory build() {
            return this.factory;
        }
    }
}
