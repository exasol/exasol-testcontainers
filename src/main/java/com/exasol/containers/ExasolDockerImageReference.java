package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_DOCKER_IMAGE_ID;
import static java.lang.Integer.parseInt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reference to an Exasol Docker image.
 */
public class ExasolDockerImageReference {
    private static final Pattern DOCKER_IMAGE_VERSION_PATTERN = Pattern
            .compile("(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:-d(\\d+))?");
    private final String reference;

    private ExasolDockerImageReference(final String reference) {
        this.reference = reference;
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
     * </ul>
     * <p>
     * Anything else is treated like a regular Docker image reference string.
     * </p>
     *
     * @param reference docker image reference or Exasol version number
     * @return reference to a {@code docker-db} image containing Exasol
     */
    public static ExasolDockerImageReference parse(final String reference) {
        final Matcher matcher = DOCKER_IMAGE_VERSION_PATTERN.matcher(reference);
        if (matcher.matches()) {
            final int major = parseInt(matcher.group(1));
            final int minor = (matcher.group(2)) == null ? 0 : parseInt(matcher.group(2));
            final int fix = (matcher.group(3)) == null ? 0 : parseInt(matcher.group(3));
            final int imageRevision = (matcher.group(4)) == null ? 1 : parseInt(matcher.group(4));
            return new ExasolDockerImageReference(
                    EXASOL_DOCKER_IMAGE_ID + ":" + major + "." + minor + "." + fix + "-d" + imageRevision);
        } else {
            return new ExasolDockerImageReference(reference);
        }
    }
}
