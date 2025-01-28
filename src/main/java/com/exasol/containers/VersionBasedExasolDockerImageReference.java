package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_DOCKER_IMAGE_ID;

import java.util.Objects;

/**
 * Docker image reference for all Exasol docker images that have the established standard layout.
 * <p>
 * Such a number has three parts: major, minor, fix.
 * </p>
 * <p>
 * Additionally there can be a revision number for the docker image. This is optional and only really used in cases
 * where a docker image with the same database version had to be re-published. Typically if an error occurred during
 * image build.
 * </p>
 */
class VersionBasedExasolDockerImageReference implements ExasolDockerImageReference {
    private static final int DEFAULT_DOCKER_IMAGE_REVISION = 1;
    public static final int VERSION_NOT_PRESENT = -1;
    public static final String PREFIX_NOT_PRESENT = null;
    public static final String SUFFIX_NOT_PRESENT = null;
    private final String dockerImageId;
    private final int major;
    private final int minor;
    private final int fix;
    private final int dockerImageRevision;
    private final String prefix;
    private final String suffix;
    private final String suffixSeparator;

    /**
     * Create a docker image reference from a fully specified version.
     *
     * @param major               major database version
     * @param minor               minor database version
     * @param fix                 fix version (aka. "patch level")
     * @param prefix              prefix (like "prerelease")
     * @param suffixSeparator     separator between version and suffix
     * @param suffix              suffix like "RC1" or "beta"
     * @param dockerImageRevision revision number of the docker image
     * @deprecated Use {@link #builder()}.
     */
    @Deprecated(forRemoval = true)
    public VersionBasedExasolDockerImageReference(final int major, final int minor, final int fix, final String prefix,
            final String suffixSeparator, final String suffix, final int dockerImageRevision) {
        this(builder().major(major).minor(minor).fix(fix).prefix(prefix).suffixSeparator(suffixSeparator).suffix(suffix)
                .dockerImageRevision(dockerImageRevision));
    }

    public static Builder builder() {
        return new Builder();
    }

    private VersionBasedExasolDockerImageReference(final Builder builder) {
        this.dockerImageId = Objects.requireNonNull(builder.dockerImageId, "dockerImageId");
        this.major = builder.major;
        this.minor = builder.minor;
        this.fix = builder.fix;
        this.prefix = builder.prefix;
        this.suffixSeparator = builder.suffixSeparator;
        this.suffix = builder.suffix;
        this.dockerImageRevision = ((builder.dockerImageRevision == VERSION_NOT_PRESENT) && !isExasolSevenOrLater())
                ? DEFAULT_DOCKER_IMAGE_REVISION
                : builder.dockerImageRevision;
    }

    @Override
    public int getMajor() {
        return this.major;
    }

    @Override
    public boolean hasMajor() {
        return this.major != VERSION_NOT_PRESENT;
    }

    @Override
    public int getMinor() {
        return this.minor;
    }

    @Override
    public boolean hasMinor() {
        return this.minor != VERSION_NOT_PRESENT;
    }

    @Override
    public int getFixVersion() {
        return this.fix;
    }

    @Override
    public boolean hasFix() {
        return this.fix != VERSION_NOT_PRESENT;
    }

    @Override
    public int getDockerImageRevision() {
        return this.dockerImageRevision;
    }

    @Override
    public boolean hasDockerImageRevision() {
        return this.dockerImageRevision != VERSION_NOT_PRESENT;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public boolean hasPrefix() {
        return this.prefix != null;
    }

    @Override
    public String getSuffix() {
        return this.suffix;
    }

    @Override
    public boolean hasSuffix() {
        return this.suffix != null;
    }

    @Override
    public String toString() {
        return dockerImageId + ":" //
                + constructPrefixPart() //
                + constructVersionPart() //
                + constructOptionalSuffixPart() //
                + constructOptionalDockerImageRevisionPart();
    }

    private String constructPrefixPart() {
        return this.hasPrefix() ? this.prefix + "-" : "";
    }

    private String constructVersionPart() {
        return this.major + "." + this.minor + "." + this.fix;
    }

    private String constructOptionalSuffixPart() {
        return this.hasSuffix() ? this.suffixSeparator + this.suffix : "";
    }

    private String constructOptionalDockerImageRevisionPart() {
        if (isExasolSevenOrLater()) {
            return this.hasDockerImageRevision() ? "-d" + this.dockerImageRevision : "";
        } else {
            return "-d" + (this.hasDockerImageRevision() ? this.dockerImageRevision : DEFAULT_DOCKER_IMAGE_REVISION);
        }
    }

    private boolean isExasolSevenOrLater() {
        return this.major >= 7;
    }

    public static class Builder {
        private String dockerImageId = EXASOL_DOCKER_IMAGE_ID;
        private int major;
        private int minor;
        private int fix;
        private int dockerImageRevision;
        private String prefix;
        private String suffix;
        private String suffixSeparator;

        private Builder() {
        }

        /**
         * Set docker image ID, default: {@code exasol/docker-db}.
         * 
         * @param dockerImageId docker image ID
         * @return self
         */
        public Builder dockerImageId(final String dockerImageId) {
            this.dockerImageId = dockerImageId;
            return this;
        }

        /**
         * Set major database version.
         * 
         * @param major major database version
         * @return self
         */
        public Builder major(final int major) {
            this.major = major;
            return this;
        }

        /**
         * Set minor database version.
         * 
         * @param minor minor database version
         * @return self
         */
        public Builder minor(final int minor) {
            this.minor = minor;
            return this;
        }

        /**
         * Set fix version (aka. "patch level").
         * 
         * @param fix fix version
         * @return self
         */
        public Builder fix(final int fix) {
            this.fix = fix;
            return this;
        }

        /**
         * Set revision number of the docker image.
         * 
         * @param dockerImageRevision revision number of the docker image
         * @return self
         */
        public Builder dockerImageRevision(final int dockerImageRevision) {
            this.dockerImageRevision = dockerImageRevision;
            return this;
        }

        /**
         * Set prefix (like "prerelease").
         * 
         * @param prefix prefix
         * @return self
         */
        public Builder prefix(final String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Set suffix like "RC1" or "beta".
         * 
         * @param suffix suffix
         * @return self
         */
        public Builder suffix(final String suffix) {
            this.suffix = suffix;
            return this;
        }

        /**
         * Set separator between version and suffix.
         * 
         * @param suffixSeparator separator between version and suffix
         * @return self
         */
        public Builder suffixSeparator(final String suffixSeparator) {
            this.suffixSeparator = suffixSeparator;
            return this;
        }

        public VersionBasedExasolDockerImageReference build() {
            return new VersionBasedExasolDockerImageReference(this);
        }
    }
}
