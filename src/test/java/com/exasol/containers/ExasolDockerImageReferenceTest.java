package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("fast")
class ExasolDockerImageReferenceTest {
    // [utest->dsn~shortened-docker-image-references~2]
    @CsvSource({ //
            "7, exasol/docker-db:7.0.0", //
            "6.1, exasol/docker-db:6.1.0-d1", //
            "6.2.7, exasol/docker-db:6.2.7-d1", //
            "6.0.4-d2, exasol/docker-db:6.0.4-d2", //
            "1.222.333-d444, exasol/docker-db:1.222.333-d444", //
            "7.1-RC1, exasol/docker-db:7.1.0-RC1", //
            "8-alpha, exasol/docker-db:8.0.0-alpha", //
            "8.1.55-alpha-d1, exasol/docker-db:8.1.55-alpha-d1", //
            "8.12.9-delta-d77, exasol/docker-db:8.12.9-delta-d77", // "
            "8.12.9-d1x-d2, exasol/docker-db:8.12.9-d1x-d2", // "
            "7.1.rc1, exasol/docker-db:7.1.0.rc1", //
            "7.1.rc1-d4, exasol/docker-db:7.1.0.rc1-d4", //
            // pre-release versions
            "prerelease-8.17.0, exasol/docker-db:prerelease-8.17.0", //
            // image references with repository:
            "docker-db:8, exasol/docker-db:8.0.0", //
            "docker-db:8.3, exasol/docker-db:8.3.0", //
            "docker-db:8.4.5, exasol/docker-db:8.4.5", //
            "docker-db:8.4.5-d6, exasol/docker-db:8.4.5-d6", //
            "docker-db:9-beta, exasol/docker-db:9.0.0-beta", //
            "exasol/docker-db:8, exasol/docker-db:8.0.0", //
            "exasol/docker-db:8.3, exasol/docker-db:8.3.0", //
            "exasol/docker-db:8.4.5, exasol/docker-db:8.4.5", //
            "exasol/docker-db:8.4.5-d6, exasol/docker-db:8.4.5-d6", //
            "exasol/docker-db:9.3-alpha, exasol/docker-db:9.3.0-alpha", //
            // unconventional image references:
            "foo/bar:latest, foo/bar:latest", //
            "baz/zoo:1.2.3.4, baz/zoo:1.2.3.4" //
    })
    @ParameterizedTest
    void testToString(final String input, final String expectedReference) {
        assertThat(DockerImageReferenceFactory.parse(input).toString(), equalTo(expectedReference));
    }

    // [utest->dsn~shortened-docker-image-references~2]
    @CsvSource({ //
            "7, 7", //
            "6.1, 6", //
            "docker-db:8, 8", //
            "docker-db:9.3, 9", //
            "docker-db:10.4.2-d13, 10", //
            "exasol/docker-db:6.2.7-d1, 6", //
            "exasol/docker-db:7.0.1, 7", //
            "exasol/docker-db:8.3-alpha, 8" })
    @ParameterizedTest
    void testGetMajorVersion(final String input, final int expectedVersion) {
        final ExasolDockerImageReference reference = DockerImageReferenceFactory.parse(input);
        assertAll(() -> assertThat(reference.getMajor(), equalTo(expectedVersion)),
                () -> assertThat(reference.hasMajor(), equalTo(true)));

    }

    // [utest->dsn~shortened-docker-image-references~2]
    @CsvSource({ //
            "7, 0", //
            "6.1, 1", //
            "docker-db:8, 0", //
            "docker-db:9.3, 3", //
            "docker-db:10.44.2-d13, 44", //
            "exasol/docker-db:6.2.7-d1, 2", //
            "exasol/docker-db:7.0.1, 0", //
            "exasol/docker-db:8.3-alpha, 3" })
    @ParameterizedTest
    void testGetMinorVersion(final String input, final int expectedVersion) {
        final ExasolDockerImageReference reference = DockerImageReferenceFactory.parse(input);
        assertAll(() -> assertThat(reference.getMinor(), equalTo(expectedVersion)),
                () -> assertThat(reference.hasMinor(), equalTo(true)));
    }

    // [utest->dsn~shortened-docker-image-references~2]
    @CsvSource({ //
            "7, 0", //
            "6.1, 0", //
            "docker-db:8, 0", //
            "docker-db:9.3, 0", //
            "docker-db:10.44.2-d13, 2", //
            "exasol/docker-db:6.2.7-d1, 7", //
            "exasol/docker-db:7.0.1, 1", //
            "exasol/docker-db:8.3.62-alpha, 62" //
    })
    @ParameterizedTest
    void testGetFixVersion(final String input, final int expectedVersion) {
        final ExasolDockerImageReference reference = DockerImageReferenceFactory.parse(input);
        assertAll(() -> assertThat(reference.getFixVersion(), equalTo(expectedVersion)),
                () -> assertThat(reference.hasFix(), equalTo(true)));
    }

    // [utest->dsn~shortened-docker-image-references~2]
    @CsvSource({ //
            "7,", //
            "6.1, 1", //
            "docker-db:8,", //
            "docker-db:9.3,", //
            "docker-db:10.44.2-d13, 13", //
            "exasol/docker-db:6.2.7-d1, 1", //
            "exasol/docker-db:7.0.1,", //
            "exasol/docker-db:8.3-alpha-d144, 144", //
            "exasol/docker-db:8.3-d4x-d144, 144", //
            "exasol/docker-db:8.3-d3-d7, 7"//
    })

    @ParameterizedTest
    void testGetDockerImageRevision(final String input, final Integer expectedVersion) {
        final ExasolDockerImageReference reference = DockerImageReferenceFactory.parse(input);
        if (expectedVersion == null) {
            assertThat("no docker image revision expected", reference.hasDockerImageRevision(), equalTo(false));
        } else {
            assertAll(() -> assertThat(reference.hasDockerImageRevision(), equalTo(true)),
                    () -> assertThat("expected docker image revision", reference.getDockerImageRevision(),
                            equalTo(expectedVersion)));
        }
    }

    @CsvSource({ //
            "7,", //
            "7.1,", //
            "8-alpha, alpha", //
            "8-delta, delta", //
            "8-d6f-d1, d6f", //
            "8-d6-d1, d6", //
            "9.0.5-RC1, RC1", //
            "7.1.rc1, rc1" //
    })
    @ParameterizedTest
    void testGetSuffix(final String input, final String expectedSuffix) {
        final ExasolDockerImageReference reference = DockerImageReferenceFactory.parse(input);
        if (expectedSuffix == null) {
            assertThat("no suffix expected", reference.hasSuffix(), equalTo(false));
        } else {
            assertAll(() -> assertThat(reference.hasSuffix(), equalTo(true)),
                    () -> assertThat("expected suffix", reference.getSuffix(), equalTo(expectedSuffix)));
        }
    }

    @CsvSource({ //
            "7,", //
            "7.1,", //
            "prerelease-8, prerelease", //
            "identifier_with_underscores-8, identifier_with_underscores", //
    })
    @ParameterizedTest
    void testGetPrefix(final String input, final String expectedPrefix) {
        final ExasolDockerImageReference reference = DockerImageReferenceFactory.parse(input);
        if (expectedPrefix == null) {
            assertThat("no prefix expected", reference.hasPrefix(), equalTo(false));
        } else {
            assertAll(() -> assertThat(reference.hasPrefix(), equalTo(true)),
                    () -> assertThat("expected prefix", reference.getPrefix(), equalTo(expectedPrefix)));
        }
    }
}