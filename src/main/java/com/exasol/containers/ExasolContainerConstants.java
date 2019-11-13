package com.exasol.containers;

/**
 * Constants for the Exasol Docker containers (like image ID and version)
 */
public final class ExasolContainerConstants {
    public static final String EXASOL_DOCKER_IMAGE_VERSION = "6.2.2-d1";
    public static final String EXASOL_DOCKER_IMAGE_ID = "exasol/docker-db";
    public static final String EXASOL_DOCKER_IMAGE_REFERENCE = EXASOL_DOCKER_IMAGE_ID + ":"
            + EXASOL_DOCKER_IMAGE_VERSION;
    public static final String DEFAULT_ADMIN_USER = "SYS";
    // The following assignment intentionally contains the initial password for the database administrator.
    // Keep in mind that this project deals with disposable containers that should only be used in integration tests.
    @SuppressWarnings("squid:S2068")
    public static final String DEFAULT_SYS_USER_PASSWORD = "exasol";
    static final int CONTAINER_INTERNAL_DATABASE_PORT = 8888;
    static final int CONTAINER_INTERNAL_BUCKETFS_PORT = 6583;

    private ExasolContainerConstants() {
        // prevent instantiation
    }
}