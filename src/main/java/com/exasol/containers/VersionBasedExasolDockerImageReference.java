package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_DOCKER_IMAGE_ID;

import java.util.Optional;

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
public class VersionBasedExasolDockerImageReference implements ExasolDockerImageReference {
    private static final int VERSION_NOT_PRESENT = -1;
    private final int major;
    private final int minor;
    private final int fix;
    private final int dockerImageRevision;

    /**
     * Create a docker image reference from a fully specified version.
     *
     * @param major               major database version
     * @param minor               minor database version
     * @param fix                 fix version (aka. "patch level")
     * @param dockerImageRevision revision number of the docker image
     */
    public VersionBasedExasolDockerImageReference(final int major, final int minor, final int fix,
            final int dockerImageRevision) {
        this.major = major;
        this.minor = minor;
        this.fix = fix;
        this.dockerImageRevision = dockerImageRevision;
    }

    /**
     * Create a version omitting the optional Docker image revision.
     * <p>
     * Since for {@code docker-db} versions below 7 the image revision was mandatory, the revision automatically
     * defaults to 1.
     * </p>
     *
     * @param major major database version
     * @param minor minor database version
     * @param fix   fix version (aka. "patch level")
     */
    public VersionBasedExasolDockerImageReference(final int major, final int minor, final int fix) {
        this(major, minor, fix, (major < 7) ? 1 : VERSION_NOT_PRESENT);
    }

    @Override
    @Deprecated(since = "3.4.1")
    public Optional<Integer> getMajorVersion() {
        return (this.major == VERSION_NOT_PRESENT) ? Optional.empty() : Optional.of(this.major);
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
    public String toString() {
        if (!this.hasDockerImageRevision() && (this.major >= 7)) {
            return EXASOL_DOCKER_IMAGE_ID + ":" + this.major + "." + this.minor + "." + this.fix;
        }
        return EXASOL_DOCKER_IMAGE_ID + ":" + this.major + "." + this.minor + "." + this.fix + "-d"
                + this.dockerImageRevision;
    }
}