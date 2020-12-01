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
    private static final Pattern MAJOR_VERSION_PATTERN = Pattern
            .compile(Pattern.quote(EXASOL_DOCKER_IMAGE_ID) + ":(\\d+).*");
    private final String reference;

    private ExasolDockerImageReference(final String reference) {
        this.reference = reference;
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
            final int minor = (matcher.group(2)) == null ? 0 : parseInt(matcher.group(2));
            final int fix = (matcher.group(3)) == null ? 0 : parseInt(matcher.group(3));
            final String exasolVersion = major + "." + minor + "." + fix;
            if (matcher.group(4) == null) {
                if (major < 7) {
                    return createPreSevenVersionWithDefaultImageRevision(exasolVersion);
                } else {
                    return createSevenPlusVersionWithoutImageRevision(exasolVersion);
                }
            }
            final int imageRevision = (matcher.group(4)) == null ? 1 : parseInt(matcher.group(4));
            return createVersionWithImageRevision(exasolVersion, imageRevision);
        } else {
            return new ExasolDockerImageReference(reference);
        }
    }

    private static ExasolDockerImageReference createPreSevenVersionWithDefaultImageRevision(
            final String exasolVersion) {
        return new ExasolDockerImageReference(EXASOL_DOCKER_IMAGE_ID + ":" + exasolVersion + "-d1");
    }

    private static ExasolDockerImageReference createVersionWithImageRevision(final String exasolVersion,
            final int imageRevision) {
        return new ExasolDockerImageReference(EXASOL_DOCKER_IMAGE_ID + ":" + exasolVersion + "-d" + imageRevision);
    }

    private static ExasolDockerImageReference createSevenPlusVersionWithoutImageRevision(final String exasolVersion) {
        return new ExasolDockerImageReference(EXASOL_DOCKER_IMAGE_ID + ":" + exasolVersion);
    }

    /**
     * Get the major version of the {@code exasol/docker-db} image if possible.
     *
     * <p>
     * If a different image is used, no version is detected.
     * </p>
     *
     * @return major version number of the docker image.
     */
    public Optional<Integer> getMajorVersion() {
        final Matcher matcher = MAJOR_VERSION_PATTERN.matcher(this.reference);
        if (matcher.matches()) {
            return Optional.of(parseInt(matcher.group(1)));
        } else {
            return Optional.empty();
        }
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