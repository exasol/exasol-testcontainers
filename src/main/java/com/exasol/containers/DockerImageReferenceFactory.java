package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.DOCKER_IMAGE_OVERRIDE_PROPERTY;
import static com.exasol.containers.VersionBasedExasolDockerImageReference.*;
import static java.lang.Integer.parseInt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for Docker image references.
 */
public final class DockerImageReferenceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerImageReferenceFactory.class);

    private static final String REPOSITORY_PATTERN = "(?:(?:exasol/)?docker-db:)?";
    private static final String CUSTOM_IMAGE_PATTERN = "(?:([\\w./-]+):)";
    private static final String VERSION_PREFIX_PATTERN = "(?:(\\w+)-)?";
    private static final String EXASOL_VERSION_PATTERN = "(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?"; // (partially optional)
    private static final String SUFFIX_PATTERN = "(?:([-.])([a-zA-Z]\\w*))??";
    private static final String DOCKER_IMAGE_REVISION_PATTERN = "(?:-d(\\d+))?";
    private static final Pattern OFFICIAL_DOCKER_IMAGE_VERSION_PATTERN = Pattern.compile(REPOSITORY_PATTERN
            + VERSION_PREFIX_PATTERN + EXASOL_VERSION_PATTERN + SUFFIX_PATTERN + DOCKER_IMAGE_REVISION_PATTERN);
    private static final Pattern CUSTOM_DOCKER_IMAGE_VERSION_PATTERN = Pattern
            .compile(CUSTOM_IMAGE_PATTERN + EXASOL_VERSION_PATTERN + SUFFIX_PATTERN);

    private DockerImageReferenceFactory() {
        // prevent instantiation
    }

    static ExasolDockerImageReference parseOverridable(final String imageName, final boolean allowOverride) {
        return parse(allowOverride //
                ? versionFromSystemPropertyOrIndividual(imageName)
                : imageName);
    }

    /**
     * Return docker image version defined by system property
     * {@link com.exasol.containers.ExasolContainerConstants#DOCKER_IMAGE_OVERRIDE_PROPERTY} or the specified individual
     * version. The purpose is to minimize the total number of different docker images in order to limit the required
     * disk space.
     *
     * @param individual individual version of docker image
     * @return version from system property
     */
    public static String versionFromSystemPropertyOrIndividual(final String individual) {
        final String fromProperty = System.getProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY);
        if (fromProperty != null) {
            return fromProperty;
        }
        LOGGER.info("System property '{}' is not set. Using docker image version '{}'.", //
                DOCKER_IMAGE_OVERRIDE_PROPERTY, individual);
        return individual;
    }

    /**
     * Create a new instance of an {@link ExasolDockerImageReference} by parsing a reference string.
     * <p>
     * The following shortened reference strings are are supported and reference the standard Exasol {@code docker-db}:
     * </p>
     * <ul>
     * <li>{@code &lt;major&gt;} (7)</li>
     * <li>{@code &lt;major&gt;.&lt;minor&gt;} (7.1)</li>
     * <li>{@code &lt;major&gt;.&lt;minor&gt;.&lt;fix&gt;} (7.1.5)</li>
     * <li>{@code &lt;major&gt;.&lt;minor&gt;.&lt;fix&gt;-&lt;docker-image-revision&gt;} (7.1.5-d2)</li>
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
        final Matcher matcher = OFFICIAL_DOCKER_IMAGE_VERSION_PATTERN.matcher(reference);
        if (matcher.matches()) {
            return buildOfficialImage(matcher);
        }
        final Matcher customImageMatcher = CUSTOM_DOCKER_IMAGE_VERSION_PATTERN.matcher(reference);
        if (customImageMatcher.matches()) {
            return buildCustomImage(customImageMatcher);
        } else {
            return new LiteralExasolDockerImageReference(reference);
        }
    }

    private static ExasolDockerImageReference buildOfficialImage(final Matcher matcher) {
        return VersionBasedExasolDockerImageReference.builder() //
                .major(parseInt(matcher.group(2))) //
                .minor((matcher.group(3) == null) ? 0 : parseInt(matcher.group(3))) //
                .fix((matcher.group(4) == null) ? 0 : parseInt(matcher.group(4))) //
                .prefix((matcher.group(1) == null) ? PREFIX_NOT_PRESENT : matcher.group(1)) //
                .suffixSeparator((matcher.group(5) == null) ? SUFFIX_NOT_PRESENT : matcher.group(5)) //
                .suffix((matcher.group(6) == null) ? SUFFIX_NOT_PRESENT : matcher.group(6)) //
                .dockerImageRevision((matcher.group(7) == null) ? VERSION_NOT_PRESENT : parseInt(matcher.group(7))) //
                .build();
    }

    private static ExasolDockerImageReference buildCustomImage(final Matcher matcher) {
        return VersionBasedExasolDockerImageReference.builder() //
                .dockerImageId(matcher.group(1)) //
                .major(parseInt(matcher.group(2))) //
                .minor((matcher.group(3) == null) ? 0 : parseInt(matcher.group(3))) //
                .fix((matcher.group(4) == null) ? 0 : parseInt(matcher.group(4))) //
                .prefix(null) //
                .suffixSeparator((matcher.group(5) == null) ? SUFFIX_NOT_PRESENT : matcher.group(5)) //
                .suffix((matcher.group(6) == null) ? SUFFIX_NOT_PRESENT : matcher.group(6)) //
                .dockerImageRevision(VERSION_NOT_PRESENT) //
                .build();
    }
}
