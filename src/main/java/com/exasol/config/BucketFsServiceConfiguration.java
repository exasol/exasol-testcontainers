package com.exasol.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration parameters for a BucketFS service (aka. "a BucketFS").
 */
public class BucketFsServiceConfiguration {
    private final String name;
    private final int httpPort;
    private final int httpsPort;
    private final Map<String, BucketConfiguration> bucketConfigurations;

    private BucketFsServiceConfiguration(final Builder builder) {
        this.name = builder.name;
        this.httpPort = builder.httpPort;
        this.httpsPort = builder.httpsPort;
        this.bucketConfigurations = builder.bucketConfigurations;
    }

    /**
     * Get the name of the BucketFS service.
     *
     * @return bucket name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the HTTP port the BucketFS service listens on.
     *
     * @return HTTP port number
     */
    public int getHttpPort() {
        return this.httpPort;
    }

    /**
     * Get the HTTPS port the BucketFS service listens on.
     *
     * @return HTTPS port number
     */
    public int getHttpsPort() {
        return this.httpsPort;
    }

    /**
     * Get the configuration of a bucket.
     *
     * @param bucketName name of the bucket
     * @return
     */
    public BucketConfiguration getBucketConfiguration(final String bucketName) {
        if (this.bucketConfigurations.containsKey(bucketName)) {
            return this.bucketConfigurations.get(bucketName);
        } else {
            throw new IllegalArgumentException("Bucket \"" + bucketName + "\" does not exist in configuration");
        }
    }

    /**
     * Get a builder for a {@link BucketFsServiceConfiguration}.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link BucketFsServiceConfiguration} instances.
     */
    public static class Builder {
        private String name = "bfsdefault";
        private int httpPort = 0;
        private int httpsPort = 0;
        private final Map<String, BucketConfiguration> bucketConfigurations = new HashMap<>();

        /**
         * Set the name of the bucket.
         *
         * @param name bucket name.
         * @return Builder instance for fluent programming
         */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Set the HTTP port the BucketFS service listens on.
         *
         * @param httpPort HTTP port
         * @return Builder instance for fluent programming
         */
        public Builder httpPort(final int httpPort) {
            this.httpPort = httpPort;
            return this;
        }

        /**
         * Set the HTTPS port the BucketFS service listens on.
         *
         * @param httpPort HTTPS port
         * @return Builder instance for fluent programming
         */
        public Builder httpsPort(final int httpsPort) {
            this.httpsPort = httpsPort;
            return this;
        }

        /**
         * Add the configuration of a bucket.
         *
         * @param bucketConfiguration configuration of a bucket
         * @return Builder instance for fluent programming
         */
        public Builder addBucketConfiguration(final BucketConfiguration bucketConfiguration) {
            this.bucketConfigurations.put(bucketConfiguration.getName(), bucketConfiguration);
            return this;
        }

        /**
         * Create a new instance of a {@link BucketConfiguration}.
         *
         * @return bucket configuration
         */
        public BucketFsServiceConfiguration build() {
            return new BucketFsServiceConfiguration(this);
        }
    }
}
