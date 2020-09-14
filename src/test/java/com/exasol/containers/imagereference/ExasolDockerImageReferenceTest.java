package com.exasol.containers.imagereference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DockerImageReferenceFactoryTest {
    @CsvSource({ //
            "7, exasol/docker-db:7.0.0-d1", //
            "6.1, exasol/docker-db:6.1.0-d1", //
            "6.2.7, exasol/docker-db:6.2.7-d1", //
            "7.0.4-d2, exasol/docker-db:7.0.4-d2", //
            "111.222.333-d444, exasol/docker-db:111.222.333-d444", //
            "foo/bar:latest, foo/bar:latest", //
            "baz/zoo:1.2.3.4, baz/zoo:1.2.3.4", //
            "exasol/docker-db:7, exasol/docker-db:7.0.0-d1" })
    @ParameterizedTest
    void toString(final String input, final String expectedReference) {
        assertThat(DockerImageReferenceFactory.getInstance().parse(input).toString(), equalTo(expectedReference));
    }
}