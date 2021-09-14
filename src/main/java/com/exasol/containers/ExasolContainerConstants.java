package com.exasol.containers;

import java.util.Set;

/**
 * Constants for the Exasol Docker containers (like image ID and version).
 */
public final class ExasolContainerConstants {
    public static final String EXASOL_DOCKER_IMAGE_VERSION = "7.1.1";
    public static final String EXASOL_DOCKER_IMAGE_ID = "exasol/docker-db";
    public static final String EXASOL_DOCKER_IMAGE_REFERENCE = EXASOL_DOCKER_IMAGE_ID + ":"
            + EXASOL_DOCKER_IMAGE_VERSION;
    public static final String DEFAULT_ADMIN_USER = "SYS";
    // The following assignment intentionally contains the initial password for the database administrator.
    // Keep in mind that this project deals with disposable containers that should only be used in integration tests.
    @SuppressWarnings("squid:S2068")
    public static final String DEFAULT_SYS_USER_PASSWORD = "exasol";
    @SuppressWarnings("squid:S1075") // This is the default URI where EXAConf is supposed to be located.
    public static final String CLUSTER_CONFIGURATION_PATH = "/exa/etc/EXAConf";
    @SuppressWarnings("squid:S1075") // This is the parent directory of all logs in the Docker version of Exasol
    public static final String EXASOL_LOGS_PATH = "/exa/logs";
    public static final String EXASOL_CORE_DAEMON_LOGS_PATH = EXASOL_LOGS_PATH + "/cored";
    public static final String NAME = "exasol";
    public static final String JDBC_DRIVER_CLASS = "com.exasol.jdbc.EXADriver";
    public static final String BUCKETFS_DAEMON_LOG_FILENAME_PATTERN = "bucketfsd.*.log";
    public static final Set<String> SUPPORTED_ARCHIVE_EXTENSIONS = Set.of(".tar", ".tgz", ".tar.gz", ".zip");
    public static final String DOCKER_IMAGE_OVERRIDE_PROPERTY = "com.exasol.dockerdb.image";
    static final int DEFAULT_CONTAINER_INTERNAL_DATABASE_PORT = 8888;
    static final int DEFAULT_CONTAINER_INTERNAL_DATABASE_PORT_V7_AND_ABOVE = 8563;
    static final int DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT = 6583;
    static final int DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT_V7_AND_ABOVE = 2580;
    static final int DEFAULT_CONTAINER_INTERNAL_RPC_PORT = 443;

    private ExasolContainerConstants() {
        // prevent instantiation
    }
}