package com.exasol.containers;

import static com.exasol.containers.VersionBasedExasolDockerImageReference.SUFFIX_NOT_PRESENT;
import static com.exasol.containers.VersionBasedExasolDockerImageReference.VERSION_NOT_PRESENT;
import static java.lang.Integer.parseInt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory for Docker image references.
 */
public final class DockerImageReferenceFactory {
    private static final Pattern DOCKER_IMAGE_VERSION_PATTERN = //
            Pattern.compile("(?:(?:exasol/)?(?:docker-db:))?" // prefix (optional)
                    + "(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?" // Exasol version (partially optional)
                    + "(?:-(\\w+))??" // suffix (optional)
                    + "(?:-d(\\d+))?"); // docker image revision (optional)

    private DockerImageReferenceFactory() {
        // prevent instantiation
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
    // [impl->dsn~shortened-docker-image-references~2]
    public static ExasolDockerImageReference parse(final String reference) {
        final Matcher matcher = DOCKER_IMAGE_VERSION_PATTERN.matcher(reference);
        if (matcher.matches()) {
            final int major = parseInt(matcher.group(1));
            final int minor = (matcher.group(2) == null) ? 0 : parseInt(matcher.group(2));
            final int fix = (matcher.group(3) == null) ? 0 : parseInt(matcher.group(3));
            final String suffix = (matcher.group(4) == null) ? SUFFIX_NOT_PRESENT : matcher.group(4);
            final int dockerImageRevision = (matcher.group(5) == null) ? VERSION_NOT_PRESENT
                    : parseInt(matcher.group(5));
            return new VersionBasedExasolDockerImageReference(major, minor, fix, suffix, dockerImageRevision);
        } else {
            return new LiteralExasolDockerImageReference(reference);
        }
    }
}