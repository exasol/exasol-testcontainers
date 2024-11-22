package com.exasol.containers;

import java.nio.file.Path;

/**
 * Constants for the Exasol Docker containers (like image ID and version).
 */
public final class ExasolContainerConstants {
    /** Version of the Exasol Docker image */
    public static final String EXASOL_DOCKER_IMAGE_VERSION = "7.1.30";

    /** Reference name of the Exasol Docker image */
    public static final String EXASOL_DOCKER_IMAGE_ID = "exasol/docker-db";

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

    /** Name of the property with which the docker image name can be overridden */
    public static final String DOCKER_IMAGE_OVERRIDE_PROPERTY = "com.exasol.dockerdb.image";

    /** Default database port for Exasol versions in Docker before 7.0 */
    static final int DEFAULT_CONTAINER_INTERNAL_DATABASE_PORT = 8888;

    /** Default database port for Exasol versions in Docker from 7.0 on */
    static final int DEFAULT_CONTAINER_INTERNAL_DATABASE_PORT_V7_AND_ABOVE = 8563;

    /** Default BucketFS port for Exasol versions in Docker before 7.0 */
    static final int DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT = 6583;

    /** Default BucketFS port for Exasol versions in Docker from 7.0 on (unencrypted) */
    static final int DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT_V7_AND_ABOVE = 2580;

    /** Default BucketFS port for Exasol versions in Docker from 8.29.1 on (TLS) */
    static final int DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT_V8_29_AND_ABOVE = 2581;

    /** Default RPC port for Exasol versions in Docker */
    static final int DEFAULT_CONTAINER_INTERNAL_RPC_PORT = 443;

    /** Maximum allowed offset between container time and host time in milliseconds */
    static final int MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS = 2000;

    /** OS specific temporary directory */
    public static final Path SYSTEM_TEMP_DIR = Path.of(System.getProperty("java.io.tmpdir"));

    /** Default directory for temporary credentials */
    public static final Path CACHE_DIRECTORY = SYSTEM_TEMP_DIR.resolve("exasol_testcontainers");

    /** SSH port allowing connections to docker container */
    public static final int SSH_PORT = 20002;

    /** User name for SSH connections to docker container */
    public static final String SSH_USER = "root";

    private ExasolContainerConstants() {
        // prevent instantiation
    }
}
