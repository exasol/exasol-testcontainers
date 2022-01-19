package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.DOCKER_IMAGE_OVERRIDE_PROPERTY;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String dockerImage = System.getProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY);
        System.clearProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY);
        
        ExasolContainer<? extends ExasolContainer<?>> containerV62 = new ExasolContainer<>("6.2.7-d1");

        try {
            var exception = assertThrows(ContainerLaunchException.class, containerV62::start);
            assertThat(exception.getMessage(), containsString("E-ETC-13"));
        } finally {
            if (dockerImage != null) {
                System.setProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY, dockerImage);
            }
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