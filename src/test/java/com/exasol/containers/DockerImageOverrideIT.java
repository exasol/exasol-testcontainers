package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class DockerImageOverrideIT {
    private static final String NON_RECENT_VERSION = "6.2.11";

    // [itest->dsn~override-docker-image-via-java-property~1]
    @Test
    void testImageOverride() throws SQLException {
        System.setProperty(ExasolContainer.DOCKER_IMAGE_OVERRIDE_PROPERTY, NON_RECENT_VERSION);
        try (final ExasolContainer<? extends ExasolContainer<?>> exasol = new ExasolContainer<>()) {
            exasol.withRequiredServices().start();
            final Connection connection = exasol.createConnection();
            final String productVersion = connection.getMetaData().getDatabaseProductVersion();
            exasol.stop();
            assertThat(productVersion, containsString(NON_RECENT_VERSION));
        }
    }
}