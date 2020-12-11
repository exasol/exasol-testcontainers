package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_DOCKER_IMAGE_ID;
import static java.lang.Integer.parseInt;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reference to an Exasol Docker image.
 */
public class ExasolDockerImageReference {
    private static final Pattern DOCKER_IMAGE_VERSION_PATTERN = //
            Pattern.compile("(?:(?:exasol/)?(?:docker-db:))?" // prefix (optional)
                    + "(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?" // Exasol version (partially optional)
                    + "(?:-d(\\d+))?"); // docker image revision (optional)
    private static final int VERSION_NOT_PRESENT = -1;
    private final String reference;
    private final int major;
    private final int minor;
    private final int fix;
    private final int dockerImageRevision;

    private ExasolDockerImageReference(final String reference) {
        this.reference = reference;
        this.major = VERSION_NOT_PRESENT;
        this.minor = VERSION_NOT_PRESENT;
        this.fix = VERSION_NOT_PRESENT;
        this.dockerImageRevision = VERSION_NOT_PRESENT;
    }

    private ExasolDockerImageReference(final int major, final int minor, final int fix, final int dockerImageRevision) {
        this.major = major;
        this.minor = minor;
        this.fix = fix;
        if (dockerImageRevision == VERSION_NOT_PRESENT) {
            if (major < 7) {
                this.dockerImageRevision = 1;
                this.reference = EXASOL_DOCKER_IMAGE_ID + ":" + major + "." + minor + "." + fix + "-d1";
            } else {
                this.dockerImageRevision = VERSION_NOT_PRESENT;
                this.reference = EXASOL_DOCKER_IMAGE_ID + ":" + major + "." + minor + "." + fix;
            }
        } else {
            this.dockerImageRevision = dockerImageRevision;
            this.reference = EXASOL_DOCKER_IMAGE_ID + ":" + major + "." + minor + "." + fix + "-d"
                    + dockerImageRevision;
        }
    }

    /**
     * Create a new instance of an {@link ExasolDockerImageReference} by parsing a reference string.
     * <p>
     * The following shortened reference strings are are supported and reference the standard Exasol {@code docker-db}:
     * </p>
     * <ul>
     * <li>&lt;major&gt; (7)</li>
     * <li>&lt;major&gt;.&lt;minor&gt; (7.1)</li>
     * <li>&lt;major&gt;.&lt;minor&gt;.&lt;fix&gt; (7.1.5)</li>
     * <li>&lt;major&gt;.&lt;minor&gt;.&lt;fix&gt;-&lt;docker-image-revision&gt; (7.1.5-d2)</li>
     * <li>All of the above prefixed by <code>docker-db:</code> or <code>exasol/docker-db:</code></li>
     * </ul>
     * <p>
     * Anything else is treated like a regular Docker image reference string.
     * </p>
     *
     * @param reference docker image reference or Exasol version number
     * @return reference to a {@code docker-db} image containing Exasol
     */
    // [impl->dsn~shortened-docker-image-references~1]
    public static ExasolDockerImageReference parse(final String reference) {
        final Matcher matcher = DOCKER_IMAGE_VERSION_PATTERN.matcher(reference);
        if (matcher.matches()) {
            final int major = parseInt(matcher.group(1));
            final int minor = (matcher.group(2) == null) ? 0 : parseInt(matcher.group(2));
            final int fix = (matcher.group(3) == null) ? 0 : parseInt(matcher.group(3));
            final int dockerImageRevision = (matcher.group(4) == null) ? VERSION_NOT_PRESENT
                    : parseInt(matcher.group(4));
            return new ExasolDockerImageReference(major, minor, fix, dockerImageRevision);
        } else {
            return new ExasolDockerImageReference(reference);
        }
    }

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
    @Deprecated
    public Optional<Integer> getMajorVersion() {
        return (this.major == VERSION_NOT_PRESENT) ? Optional.empty() : Optional.of(this.major);
    }

    /**
     * Get the major version of the {@code exasol/docker-db} image if possible.
     *
     * @return major version number of the docker image
     */
    public int getMajor() {
        return this.major;
    }

    /**
     * Check if the major version available.
     *
     * @return {@code true} if the major version was detected.
     */
    public boolean hasMajor() {
        return this.major != VERSION_NOT_PRESENT;
    }

    /**
     * Get the minor version of the {@code exasol/docker-db} image if possible.
     *
     * @return minor version number of the docker image
     */
    public int getMinor() {
        return this.minor;
    }

    /**
     * Check if the minor version is available.
     *
     * @return {@code true} if the minor version was detected or complemented.
     */
    public boolean hasMinor() {
        return this.minor != VERSION_NOT_PRESENT;
    }

    /**
     * Get the fix version of the {@code exasol/docker-db} image if possible.
     *
     * @return fix version number of the docker image
     */
    public int getFixVersion() {
        return this.fix;
    }

    /**
     * Check if the fix version is available.
     *
     * @return {@code true} if the fix version was detected or complemented.
     */
    public boolean hasFix() {
        return this.fix != VERSION_NOT_PRESENT;
    }

    /**
     * Get the revision of the {@code exasol/docker-db} image if possible.
     *
     * @return revision of the docker image
     */
    public int getDockerImageRevision() {
        return this.dockerImageRevision;
    }

    /**
     * Check if the docker image revision is available.
     *
     * @return {@code true} if the docker image revision version was detected or complemented.
     */
    public boolean hasDockerImageRevision() {
        return this.dockerImageRevision != VERSION_NOT_PRESENT;
    }

    /**
     * Get the Docker image reference for an Exasol version number.
     *
     * @return Docker image Reference
     */
    @Override
    public String toString() {
        return this.reference;
    }
}