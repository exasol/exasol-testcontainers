package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("fast")
class ExasolDockerImageReferenceTest {
    // [utest->dsn~shortened-docker-image-references~1]
    @CsvSource({ //
            "7, exasol/docker-db:7.0.0", //
            "6.1, exasol/docker-db:6.1.0-d1", //
            "6.2.7, exasol/docker-db:6.2.7-d1", //
            "6.0.4-d2, exasol/docker-db:6.0.4-d2", //
            "1.222.333-d444, exasol/docker-db:1.222.333-d444", //
            // prefixed image references:
            "docker-db:8, exasol/docker-db:8.0.0", //
            "docker-db:8.3, exasol/docker-db:8.3.0", //
            "docker-db:8.4.5, exasol/docker-db:8.4.5", //
            "docker-db:8.4.5-d6, exasol/docker-db:8.4.5-d6", //
            "exasol/docker-db:8, exasol/docker-db:8.0.0", //
            "exasol/docker-db:8.3, exasol/docker-db:8.3.0", //
            "exasol/docker-db:8.4.5, exasol/docker-db:8.4.5", //
            "exasol/docker-db:8.4.5-d6, exasol/docker-db:8.4.5-d6", //
            // unconventional image references:
            "foo/bar:latest, foo/bar:latest", //
            "baz/zoo:1.2.3.4, baz/zoo:1.2.3.4" //
    })
    @ParameterizedTest
    void toString(final String input, final String expectedReference) {
        assertThat(DockerImageReferenceFactory.parse(input).toString(), equalTo(expectedReference));
    }

    // [utest->dsn~shortened-docker-image-references~1]
    @CsvSource({ //
            "7, 7", //
            "6.1, 6", //
            "docker-db:8, 8", //
            "docker-db:9.3, 9", //
            "docker-db:10.4.2-d13, 10", //
            "exasol/docker-db:6.2.7-d1, 6", //
            "exasol/docker-db:7.0.1, 7" //
    })
    @ParameterizedTest
    void getMajorVersion(final String input, final int expectedVersion) {
        final ExasolDockerImageReference reference = DockerImageReferenceFactory.parse(input);
        assertAll(() -> assertThat(reference.getMajor(), equalTo(expectedVersion)),
                () -> assertThat(reference.hasMajor(), equalTo(true)));

    }

    // [utest->dsn~shortened-docker-image-references~1]
    @CsvSource({ //
            "7, 0", //
            "6.1, 1", //
            "docker-db:8, 0", //
            "docker-db:9.3, 3", //
            "docker-db:10.44.2-d13, 44", //
            "exasol/docker-db:6.2.7-d1, 2", //
            "exasol/docker-db:7.0.1, 0" //
    })
    @ParameterizedTest
    void getMinorVersion(final String input, final int expectedVersion) {
        final ExasolDockerImageReference reference = DockerImageReferenceFactory.parse(input);
        assertAll(() -> assertThat(reference.getMinor(), equalTo(expectedVersion)),
                () -> assertThat(reference.hasMinor(), equalTo(true)));
    }

    // [utest->dsn~shortened-docker-image-references~1]
    @CsvSource({ //
            "7, 0", //
            "6.1, 0", //
            "docker-db:8, 0", //
            "docker-db:9.3, 0", //
            "docker-db:10.44.2-d13, 2", //
            "exasol/docker-db:6.2.7-d1, 7", //
            "exasol/docker-db:7.0.1, 1" //
    })
    @ParameterizedTest
    void getFixVersion(final String input, final int expectedVersion) {
        final ExasolDockerImageReference reference = DockerImageReferenceFactory.parse(input);
        assertAll(() -> assertThat(reference.getFixVersion(), equalTo(expectedVersion)),
                () -> assertThat(reference.hasFix(), equalTo(true)));
    }

    // [utest->dsn~shortened-docker-image-references~1]
    @CsvSource({ //
            "7,", //
            "6.1, 1", //
            "docker-db:8,", //
            "docker-db:9.3,", //
            "docker-db:10.44.2-d13, 13", //
            "exasol/docker-db:6.2.7-d1, 1", //
            "exasol/docker-db:7.0.1," //
    })
    @ParameterizedTest
    void getDockerImageRevision(final String input, final Integer expectedVersion) {
        final ExasolDockerImageReference reference = DockerImageReferenceFactory.parse(input);
        if (expectedVersion == null) {
            assertThat("no docker image revision expected", reference.hasDockerImageRevision(), equalTo(false));
        } else {
            assertAll(() -> assertThat(reference.hasDockerImageRevision(), equalTo(true)),
                    () -> assertThat("expected docker image revision", reference.getDockerImageRevision(),
                            equalTo(expectedVersion)));
        }
    }
}
