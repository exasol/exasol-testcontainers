package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExasolDockerImageReferenceTest {
    @CsvSource({ //
            "7, exasol/docker-db:7.0.0-d1", //
            "6.1, exasol/docker-db:6.1.0-d1", //
            "6.2.7, exasol/docker-db:6.2.7-d1", //
            "7.0.4-d2, exasol/docker-db:7.0.4-d2", //
            "111.222.333-d444, exasol/docker-db:111.222.333-d444", //
            "foo/bar:latest, foo/bar:latest", //
            "baz/zoo:1.2.3.4, baz/zoo:1.2.3.4" })
    @ParameterizedTest
    void toString(final String input, final String expectedReference) {
        assertThat(ExasolDockerImageReference.parse(input).toString(), equalTo(expectedReference));
    }

    @CsvSource({ //
            "7, 7", //
            "6.1, 6", //
            "exasol/docker-db:6.2.7-d1, 6", //
            "exasol/docker-db:7.0.1, 7" //
    })
    @ParameterizedTest
    void getMajorVersion(final String input, final int expectedVersion) {
        assertThat(ExasolDockerImageReference.parse(input).getMajorVersion().orElseThrow(), equalTo(expectedVersion));
    }
}
