package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.DOCKER_IMAGE_OVERRIDE_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("slow")
@Testcontainers
class DockerImageOverrideIT {
    private static final String NON_RECENT_VERSION = "7.0.12";

    // [itest->dsn~override-docker-image-via-java-property~1]
    @Test
    void testImageOverride() throws SQLException {
        final String originalVersion = System.getProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY);
        System.setProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY, NON_RECENT_VERSION);
        try (final ExasolContainer<? extends ExasolContainer<?>> exasol = new ExasolContainer<>()) {
            exasol.withRequiredServices().start();
            final Connection connection = exasol.createConnection();
            final String productVersion = connection.getMetaData().getDatabaseProductVersion();
            exasol.stop();
            assertThat(productVersion, containsString(NON_RECENT_VERSION));
        } finally {
            if (originalVersion == null) {
                System.clearProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY);
            } else {
                System.setProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY, originalVersion);
            }
        }
    }
}