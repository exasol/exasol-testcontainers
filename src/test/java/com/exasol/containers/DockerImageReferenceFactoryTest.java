package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.DOCKER_IMAGE_OVERRIDE_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;
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

    @ParameterizedTest
    @CsvSource(value = { "1, exasol/docker-db:1.0.0-d1, 1, 0, 0", //
            "1.2, exasol/docker-db:1.2.0-d1, 1, 2, 0", //
            "1.2.3, exasol/docker-db:1.2.3-d1, 1, 2, 3", //
            "1.2.3-d4, exasol/docker-db:1.2.3-d4, 1, 2, 3", //
            "prefix-1.2.3, exasol/docker-db:prefix-1.2.3-d1, 1, 2, 3", //
            "1.2.3-revision, exasol/docker-db:1.2.3-revision-d1, 1, 2, 3", //
            "8.9.1, exasol/docker-db:8.9.1, 8, 9, 1", //
            "8.9.1-revision, exasol/docker-db:8.9.1-revision, 8, 9, 1", //
            "9.8.7, exasol/docker-db:9.8.7, 9, 8, 7", //
            "9.8.7-revision, exasol/docker-db:9.8.7-revision, 9, 8, 7", //
            "10.8.7, exasol/docker-db:10.8.7, 10, 8, 7", //
            "docker-db:1.2.3, exasol/docker-db:1.2.3-d1, 1, 2, 3",
            "docker-db:1.2.3-d4, exasol/docker-db:1.2.3-d4, 1, 2, 3",
            "docker-db:8.29.5, exasol/docker-db:8.29.5, 8, 29, 5",
            "docker-db:prefix-8.29.5, exasol/docker-db:prefix-8.29.5, 8, 29, 5",
            "exasol/docker-db:1.2.3, exasol/docker-db:1.2.3-d1, 1, 2, 3",
            "exasol/docker-db:1.2.3-revision, exasol/docker-db:1.2.3-revision-d1, 1, 2, 3",
            "4.5.6-x, exasol/docker-db:4.5.6-x-d1, 4, 5, 6", //
            "docker-db:8.33.0, exasol/docker-db:8.33.0, 8, 33, 0", //
            "custom-image:8, custom-image:8.0.0, 8, 0, 0", "custom-image:8.30, custom-image:8.30.0, 8, 30, 0",
            "custom-image:8.32.0, custom-image:8.32.0, 8, 32, 0",
            "custom-image:8.32.0-suffix, custom-image:8.32.0-suffix, 8, 32, 0",
            "custom/docker-db:8.32.0, custom/docker-db:8.32.0, 8, 32, 0",
            "ghcr.io/org/custom/docker-db:8.32.0, ghcr.io/org/custom/docker-db:8.32.0, 8, 32, 0",
            "ghcr.io/org/custom/docker-db:8.32.0-revision, ghcr.io/org/custom/docker-db:8.32.0-revision, 8, 32, 0",
            "ghcr.io/exasol/custom-project/docker-db:8.34.0, ghcr.io/exasol/custom-project/docker-db:8.34.0, 8, 34, 0" })
    void testParseVersion(final String version, final String expected, final int expectedMajor, final int expectedMinor,
            final int expectedFixVersion) {
        final ExasolDockerImageReference testee = DockerImageReferenceFactory.parse(version);
        assertAll(() -> assertThat("Complete version", testee.toString(), equalTo(expected)),
                () -> assertThat("Major version", testee.getMajor(), equalTo(expectedMajor)),
                () -> assertThat("Minor version", testee.getMinor(), equalTo(expectedMinor)),
                () -> assertThat("Fix version", testee.getFixVersion(), equalTo(expectedFixVersion)));
    }

    @Test
    void testParseVersionReturnsGenericReference() {
        final ExasolDockerImageReference testee = DockerImageReferenceFactory.parse("unknown-image");
        assertAll(() -> assertThat(testee.toString(), equalTo("unknown-image")), //
                () -> assertThat(testee.hasMajor(), is(false)), //
                () -> assertThat(testee.hasMinor(), is(false)), //
                () -> assertThat(testee.hasFix(), is(false)), //
                () -> assertThat(testee.hasPrefix(), is(false)), //
                () -> assertThat(testee.hasSuffix(), is(false)));
    }
}
