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

    private static String getDockerImageOverrideProperty() {
        return System.getProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY);
    }

    private static ExasolDockerImageReference getDockerImageReference() {
        return DockerImageReferenceFactory.parse(getDockerImageOverrideProperty());
    }

    /**
     * Assume that the Exasol dockerdb version a container runs with is not overridden to a version above 7.0.x
     */
    public static void assumeDockerDbVersionNotOverriddenToAboveExasolSevenZero() {
        if (getDockerImageOverrideProperty() != null) {
            final ExasolDockerImageReference dockerImageReference = getDockerImageReference();
            assumeTrue((dockerImageReference.hasMajor() && (dockerImageReference.getMajor() < 7))
                    || (dockerImageReference.hasMajor() && (dockerImageReference.getMajor() == 7)
                            && dockerImageReference.hasMinor() && (dockerImageReference.getMinor() == 0)));
        }
    }

    /**
     * Assume that the Exasol dockerdb version a container runs with is not overridden to a version below 7.1.x
     */
    public static void assumeDockerDbVersionNotOverriddenToBelowExasolSevenOne() {
        if (getDockerImageOverrideProperty() != null) {
            final ExasolDockerImageReference dockerImageReference = getDockerImageReference();
            assumeTrue((dockerImageReference.hasMajor() && (dockerImageReference.getMajor() > 7))
                    || (dockerImageReference.hasMajor() && (dockerImageReference.getMajor() == 7)
                            && dockerImageReference.hasMinor() && (dockerImageReference.getMinor() > 0)));
        }
    }

    /**
     * Assume that the Exasol dockerdb version a container runs with is not overridden to a version below 8
     */
    public static void assumeDockerDbVersionNotOverriddenToBelowExasolEight() {
        if (getDockerImageOverrideProperty() != null) {
            final ExasolDockerImageReference dockerImageReference = getDockerImageReference();
            assumeTrue((dockerImageReference.hasMajor() && (dockerImageReference.getMajor() < 8)));
        }
    }
}
