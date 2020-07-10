package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.EXASOL_DOCKER_IMAGE_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExasolDockerImageReferenceTest {
    @CsvSource({ "7, 7.0.0-d1", "6.1, 6.1.0-d1", "6.2.7, 6.2.7-d1", "7.0.4-d2, 7.0.4-d2",
            "111.222.333-d444, 111.222.333-d444" })
    @ParameterizedTest
    void toString(final String parialVersion, final String expandedVersion) {
        assertThat(new ExasolDockerImageReference(parialVersion).toString(),
                equalTo(EXASOL_DOCKER_IMAGE_ID + ":" + expandedVersion));
    }
}