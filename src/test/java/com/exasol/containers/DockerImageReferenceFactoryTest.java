package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.DOCKER_IMAGE_OVERRIDE_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junitpioneer.jupiter.ClearSystemProperty;

class DockerImageReferenceFactoryTest {

    // [itest->dsn~override-docker-image-via-java-property~1]
    /*
     * Replaced DockerImageOverrideIT by unit test as the integration test had long duration and required too much disk
     * space on GitHub for the additional docker image.
     */
    @ParameterizedTest
    @CsvSource(value = { "1.2.3, 4.5.6-x, false, exasol/docker-db:1.2.3-d1", //
            "1.2.3, 4.5.6-x, true, exasol/docker-db:4.5.6-x-d1" })
    @ClearSystemProperty(key = DOCKER_IMAGE_OVERRIDE_PROPERTY)
    void testImageOverride(final String defaultVersion, final String overrideVersion, final boolean allowOverride,
            final String expected) {
        System.setProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY, overrideVersion);
        final ExasolDockerImageReference testee = DockerImageReferenceFactory.parseOverridable(defaultVersion,
                allowOverride);
        assertThat(testee.toString(), equalTo(expected));
    }
}