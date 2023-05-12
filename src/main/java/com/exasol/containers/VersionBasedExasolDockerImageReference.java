package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_DOCKER_IMAGE_ID;

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
     */
    public VersionBasedExasolDockerImageReference(final int major, final int minor, final int fix, final String prefix,
            final String suffixSeparator, final String suffix, final int dockerImageRevision) {
        this.major = major;
        this.minor = minor;
        this.fix = fix;
        this.prefix = prefix;
        this.suffixSeparator = suffixSeparator;
        this.suffix = suffix;
        this.dockerImageRevision = ((dockerImageRevision == VERSION_NOT_PRESENT) && !isExasolSevenOrLater())
                ? DEFAULT_DOCKER_IMAGE_REVISION
                : dockerImageRevision;
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
        return EXASOL_DOCKER_IMAGE_ID + ":" //
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
}