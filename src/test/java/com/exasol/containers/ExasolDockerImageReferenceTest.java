package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        assertThat(ExasolDockerImageReference.parse(input).toString(), equalTo(expectedReference));
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
        assertThat(ExasolDockerImageReference.parse(input).getMajorVersion().orElseThrow(), equalTo(expectedVersion));
    }
}
