package com.exasol.containers.imagereference;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_DOCKER_IMAGE_ID;
import static java.lang.Integer.parseInt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reference to an Exasol Docker image.
 */
public class ExasolDockerImageReference implements DockerImageReference {
    private static final Pattern DOCKER_IMAGE_VERSION_PATTERN = Pattern
            .compile("(\\Q" + EXASOL_DOCKER_IMAGE_ID + ":\\E)?(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:-d(\\d+))?");
    private final int major;
    private final int minor;
    private final int fix;
    private final int imageRevision;

    /**
     * Create a new instance of {@link ExasolDockerImageReference}.
     * 
     * @param reference reference string to parse.
     * @throws NotAnExasolImageReferenceException if reference string is not for a official docker-db image
     */
    ExasolDockerImageReference(final String reference) throws NotAnExasolImageReferenceException {
        final Matcher matcher = DOCKER_IMAGE_VERSION_PATTERN.matcher(reference);
        if (matcher.matches()) {
            this.major = parseInt(matcher.group(2));
            this.minor = (matcher.group(3)) == null ? 0 : parseInt(matcher.group(3));
            this.fix = (matcher.group(4)) == null ? 0 : parseInt(matcher.group(4));
            this.imageRevision = (matcher.group(5)) == null ? 1 : parseInt(matcher.group(5));
        } else {
            throw new NotAnExasolImageReferenceException();
        }
    }

    /**
     * Create a new instance of {@link ExasolDockerImageReference}.
     * 
     * @param major         major version
     * @param minor         minor version
     * @param fix           version
     * @param imageRevision imageRevision
     */
    public ExasolDockerImageReference(final int major, final int minor, final int fix, final int imageRevision) {
        this.major = major;
        this.minor = minor;
        this.fix = fix;
        this.imageRevision = imageRevision;
    }

    @Override
    public String toString() {
        return EXASOL_DOCKER_IMAGE_ID + ":" + this.major + "." + this.minor + "." + this.fix + "-d"
                + this.imageRevision;
    }

    /**
     * Get the major version.
     * 
     * @return major version
     */
    public int getMajor() {
        return this.major;
    }

    /**
     * Get the minor version.
     * 
     * @return minor version
     */
    public int getMinor() {
        return this.minor;
    }

    /**
     * Get the fix version.
     * 
     * @return fix version
     */
    public int getFix() {
        return this.fix;
    }

    /**
     * Get the image revision.
     * 
     * @return image revision.
     */
    public int getImageRevision() {
        return this.imageRevision;
    }

    static class NotAnExasolImageReferenceException extends Exception {

    }
}
