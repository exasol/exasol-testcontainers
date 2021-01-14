package com.exasol.containers;

import java.util.Optional;

/**
 * Reference to an Exasol Docker image.
 */
public interface ExasolDockerImageReference {
    /**
     * Get the major version of the {@code exasol/docker-db} image if possible.
     *
     * <p>
     * If a non-standard image is used, no version is detected.
     * </p>
     *
     * @deprecated As of 3.4.1, use {@link ExasolDockerImageReference#getMajor()} and
     *             {@link ExasolDockerImageReference#hasMajor()} instead.
     *
     * @return major version number of the docker image
     */
    @Deprecated(since = "3.4.1")
    public Optional<Integer> getMajorVersion();

    /**
     * Get the major version of the {@code exasol/docker-db} image if possible.
     *
     * @return major version number of the docker image
     */
    public int getMajor();

    /**
     * Check if the major version available.
     *
     * @return {@code true} if the major version was detected.
     */
    public boolean hasMajor();

    /**
     * Get the minor version of the {@code exasol/docker-db} image if possible.
     *
     * @return minor version number of the docker image
     */
    public int getMinor();

    /**
     * Check if the minor version is available.
     *
     * @return {@code true} if the minor version was detected or complemented.
     */
    public boolean hasMinor();

    /**
     * Get the fix version of the {@code exasol/docker-db} image if possible.
     *
     * @return fix version number of the docker image
     */
    public int getFixVersion();

    /**
     * Check if the fix version is available.
     *
     * @return {@code true} if the fix version was detected or complemented.
     */
    public boolean hasFix();

    /**
     * Get the revision of the {@code exasol/docker-db} image if possible.
     *
     * @return revision of the docker image
     */
    public int getDockerImageRevision();

    /**
     * Check if the docker image revision is available.
     *
     * @return {@code true} if the docker image revision version was detected or complemented.
     */
    public boolean hasDockerImageRevision();

    /**
     * Get the Docker image reference for an Exasol version number.
     *
     * @return Docker image Reference
     */
    @Override
    public String toString();
}