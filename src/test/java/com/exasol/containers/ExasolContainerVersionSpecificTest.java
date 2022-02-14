package com.exasol.containers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ContainerLaunchException;

/**
 * Test an Exasol Container with version lower than 7.0.
 */
@Tag("fast")
class ExasolContainerVersionSpecificTest {
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER_V7 = new ExasolContainer<>("7.0.0",
            false);

    @Test
    void testContainer62x() {
        try (final ExasolContainer<? extends ExasolContainer<?>> containerV62 = new ExasolContainer<>("6.2.7-d1",
                false)) {
            final var exception = assertThrows(ContainerLaunchException.class, containerV62::start);
            assertThat(exception.getMessage(), containsString("E-ETC-13"));
        }

    }

    @Test
    void testBucketfsPortOnV7() {
        assertThat(CONTAINER_V7.getDefaultInternalBucketfsPort(), equalTo(2580));
    }

    @Test
    void testDatabasePortOnV7() {
        assertThat(CONTAINER_V7.getDefaultInternalDatabasePort(), equalTo(8563));
    }
}