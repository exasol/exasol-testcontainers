package com.exasol.containers;

/**
 * Constants for the Exasol Docker containers (like image ID and version)
 */
public final class ExasolContainerConstants {
    public static final String EXASOL_DOCKER_IMAGE_VERSION = "6.2.2-d1";
    public static final String EXASOL_DOCKER_IMAGE_ID = "exasol/docker-db";
    public static final String EXASOL_DOCKER_IMAGE_REFERENCE = EXASOL_DOCKER_IMAGE_ID + ":"
            + EXASOL_DOCKER_IMAGE_VERSION;

    private ExasolContainerConstants() {
        // prevent instantiation
    }
}