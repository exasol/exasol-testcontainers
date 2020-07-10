package com.exasol.containers;

import static java.lang.Integer.parseInt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reference to an Exasol Docker image.
 */
public class ExasolDockerImageReference {
    private static final Pattern DOCKER_IMAGE_VERSION_PATTERN = Pattern
            .compile("(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:-d(\\d+))?");
    private final int majorVersion;
    private final int minorVersion;
    private final int fixVersion;
    private final int dockerImageRevision;

    /**
     * Create a new instance of an {@link ExasolDockerImageReference} from a version string.
     * <p>
     * Supported version strings are:
     * <ul>
     * <li>&lt;major&gt; (7)</li>
     * <li>&lt;major&gt;.&lt;minor&gt; (7.1)</li>
     * <li>&lt;major&gt;.&lt;minor&gt;.&lt;fix&gt; (7.1.5)</li>
     * <li>&lt;major&gt;.&lt;minor&gt;.&lt;fix&gt;-&lt;docker-image-revision&gt; (7.1.5-d2)</li>
     * </ul>
     *
     * @param version version number
     */
    public ExasolDockerImageReference(final String version) {
        final Matcher matcher = DOCKER_IMAGE_VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            this.majorVersion = parseInt(matcher.group(1));
            this.minorVersion = (matcher.group(2)) == null ? 0 : parseInt(matcher.group(2));
            this.fixVersion = (matcher.group(3)) == null ? 0 : parseInt(matcher.group(3));
            this.dockerImageRevision = (matcher.group(4)) == null ? 1 : parseInt(matcher.group(4));
        } else {
            throw new IllegalArgumentException("Illegal Exasol Docker image version number: " + version);
        }
    }

    /**
     * Get the Docker image reference for an Exasol version number.
     *
     * @return Docker image Reference
     */
    @Override
    public String toString() {
        return ExasolContainerConstants.EXASOL_DOCKER_IMAGE_ID + ":" + this.majorVersion + "." + this.minorVersion + "."
                + this.fixVersion + "-d" + this.dockerImageRevision;
    }
}
