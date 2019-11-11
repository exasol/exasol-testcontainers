package com.exasol.config;

/**
 * Configuration parameters of a bucket in BucketFS.
 */
public class BucketConfiguration {
    private final String name;
    private final boolean publiclyReadable;
    private final String readPassword;
    private final String writePassword;
    private final BucketFsServiceConfiguration serviceConfiguration;

    public BucketConfiguration(final Builder builder) {
        this.name = builder.name;
        this.publiclyReadable = builder.publiclyReadable;
        this.readPassword = builder.readPassword;
        this.writePassword = builder.writePassword;
        this.serviceConfiguration = builder.serviceConfiguration;
    }

    /**
     * Get the name of the bucket.
     *
     * @return bucket name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the bucket read password.
     *
     * @return read password
     */
    public String getReadPassword() {
        return this.readPassword;
    }

    /**
     * Get the bucket write password.
     *
     * @return write password
     */
    public String getWritePassword() {
        return this.writePassword;
    }

    /**
     * Get a builder for a {@link BucketConfiguration}.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Check whether the bucket is publicly readable.
     *
     * @return {@code true} if the bucket contents can be read without password
     */
    public boolean isPubliclyReadable() {
        return this.publiclyReadable;
    }

    /**
     * Get the configuration of the parent BucketFS service.
     *
     * @return service configuration
     */
    public BucketFsServiceConfiguration getBucketFsServiceConfiguration() {
        return this.serviceConfiguration;
    }

    /**
     * Builder for {@link BucketConfiguration} instances.
     */
    public static class Builder {
        private String name = "default";
        private boolean publiclyReadable = false;
        private String readPassword = null;
        private String writePassword = null;
        private BucketFsServiceConfiguration serviceConfiguration;

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
         * Set the read password.
         *
         * @param readPassword password
         * @return Builder instance for fluent programming
         */
        public Builder readPassword(final String readPassword) {
            this.readPassword = readPassword;
            return this;
        }

        /**
         * Set the write password.
         *
         * @param writePassword password
         * @return Builder instance for fluent programming
         */
        public Builder writePassword(final String writePassword) {
            this.writePassword = writePassword;
            return this;
        }

        /**
         * Set whether the bucket is publicly readable.
         *
         * @param publiclyReadable {@code true} if the bucket can be read without a password
         * @return Builder instance for fluent programming
         */
        public Builder publiclyReadable(final boolean publiclyReadable) {
            this.publiclyReadable = publiclyReadable;
            return this;
        }

        /**
         * Link to the configuration of the parent BucketFS service
         *
         * @param serviceConfiguration configuration of the service
         * @return Builder instance for fluent programming
         */
        public Builder bucketFsServiceConfiguration(final BucketFsServiceConfiguration serviceConfiguration) {
            this.serviceConfiguration = serviceConfiguration;
            return this;
        }

        /**
         * Create a new instance of a {@link BucketConfiguration}.
         *
         * @return bucket configuration
         */
        public BucketConfiguration build() {
            return new BucketConfiguration(this);
        }
    }
}
