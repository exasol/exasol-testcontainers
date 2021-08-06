package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.DOCKER_IMAGE_OVERRIDE_PROPERTY;
import static org.junit.Assume.assumeTrue;

/**
 * This class contains shared assumptions about the Exasol container used in the tests.
 */
public final class ExasolContainerAssumptions {
    private ExasolContainerAssumptions() {
        // prevent instantiation
    }

    /**
     * Assume that the Exasol dockerdb version a container runs with is not overridden to a version below 7.0.0
     */
    public static void assumeDockerDbVersionNotOverriddenToBelowExasolSeven() {
        final String dockerImageProteryValue = System.getProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY);
        if (dockerImageProteryValue != null) {
            final ExasolDockerImageReference dockerImageReference = DockerImageReferenceFactory
                    .parse(dockerImageProteryValue);
            assumeTrue(dockerImageReference.hasMajor() && (dockerImageReference.getMajor() >= 7));
        }
    }
}