package com.exasol.containers;

import java.util.Set;

/**
 * Constants for the Exasol Docker containers (like image ID and version).
 */
public final class ExasolContainerConstants {
    /** Version of the Exasol Docker image */
    public static final String EXASOL_DOCKER_IMAGE_VERSION = "7.1.6";

    /** Reference name of the Exasol Docker image */
    public static final String EXASOL_DOCKER_IMAGE_ID = "exasol/docker-db";

    /** Complete Docker image reference */
    public static final String EXASOL_DOCKER_IMAGE_REFERENCE = EXASOL_DOCKER_IMAGE_ID + ":"
            + EXASOL_DOCKER_IMAGE_VERSION;

    /** Exasol initial administrator username */
    public static final String DEFAULT_ADMIN_USER = "SYS";

    // The following assignment intentionally contains the initial password for the database administrator.
    // Keep in mind that this project deals with disposable containers that should only be used in integration tests.
    /** Initial administrator password */
    @SuppressWarnings("squid:S2068")
    public static final String DEFAULT_SYS_USER_PASSWORD = "exasol";

    /** Default path of the central EXAConf configuration file. */
    @SuppressWarnings("squid:S1075")
    public static final String CLUSTER_CONFIGURATION_PATH = "/exa/etc/EXAConf";

    /** Default path of all Exasol logs */
    @SuppressWarnings("squid:S1075") // This is the parent directory of all logs in the Docker version of Exasol
    public static final String EXASOL_LOGS_PATH = "/exa/logs";

    /** Path to core daemon logs */
    public static final String EXASOL_CORE_DAEMON_LOGS_PATH = EXASOL_LOGS_PATH + "/cored";

    /** Reference name for the service used in the container factory */
    public static final String NAME = "exasol";

    /** JDBC main class */
    public static final String JDBC_DRIVER_CLASS = "com.exasol.jdbc.EXADriver";

    /** BucketFS log filename pattern */
    public static final String BUCKETFS_DAEMON_LOG_FILENAME_PATTERN = "bucketfsd.*.log";

    /** File extensions of supported (i.e. auto-expanded) archive formats in BucketFS */
    public static final Set<String> SUPPORTED_ARCHIVE_EXTENSIONS = Set.of(".tar", ".tgz", ".tar.gz", ".zip");

    /** Name of the property with which the docker image name can be overridden */
    public static final String DOCKER_IMAGE_OVERRIDE_PROPERTY = "com.exasol.dockerdb.image";

    /** Default database port for Exasol versions in Docker before 7.0 */
    static final int DEFAULT_CONTAINER_INTERNAL_DATABASE_PORT = 8888;

    /** Default database port for Exasol versions in Docker from 7.0 on */
    static final int DEFAULT_CONTAINER_INTERNAL_DATABASE_PORT_V7_AND_ABOVE = 8563;

    /** Default BucketFS port for Exasol versions in Docker before 7.0 */
    static final int DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT = 6583;

    /** Default BucketFS port for Exasol versions in Docker from 7.0 on */
    static final int DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT_V7_AND_ABOVE = 2580;

    /** Default RPC port for Exasol versions in Docker */
    static final int DEFAULT_CONTAINER_INTERNAL_RPC_PORT = 443;

    private ExasolContainerConstants() {
        // prevent instantiation
    }
}