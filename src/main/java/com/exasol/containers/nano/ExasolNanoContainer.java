package com.exasol.containers.nano;

import static com.exasol.containers.ExasolContainerConstants.*;

import java.sql.*;
import java.time.Duration;
import java.util.Properties;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.UncheckedSqlException;
import com.exasol.errorreporting.ExaError;

/**
 * Exasol Nano container for JDBC-focused integration tests.
 * <p>
 * This container intentionally supports only the SQL endpoint of {@code exasol/nano}. Use {@link ExasolContainer} when
 * tests need features such as BucketFS, UDFs, EXAoperation emulation or cluster configuration access.
 * </p>
 */
@SuppressWarnings("squid:S2160") // Superclass adds state but does not override equals() and hashCode().
public class ExasolNanoContainer extends JdbcDatabaseContainer<ExasolNanoContainer> {
    /** Timeout for JDBC connection readiness checks. */
    private static final Duration CONNECTION_WAIT_TIMEOUT = Duration.ofSeconds(30);

    /** Reference name of the Exasol Nano Docker image */
    public static final String EXASOL_NANO_DOCKER_IMAGE_ID = "exasol/nano";

    /** Default tag of the Exasol Nano Docker image */
    public static final String EXASOL_NANO_DOCKER_IMAGE_TAG = "latest";

    /** Default reference of the Exasol Nano Docker image */
    public static final String EXASOL_NANO_DOCKER_IMAGE_REFERENCE = EXASOL_NANO_DOCKER_IMAGE_ID + ":"
            + EXASOL_NANO_DOCKER_IMAGE_TAG;

    /** Default SQL port for Exasol Nano */
    public static final int EXASOL_NANO_SQL_PORT = 8563;

    /** Default Web UI port for Exasol Nano */
    public static final int EXASOL_NANO_WEB_UI_PORT = 8443;

    /** Default shared memory size for Exasol Nano */
    static final long EXASOL_NANO_SHARED_MEMORY_SIZE = 1024L * 1024L * 1024L;

    static final Duration CONNECTION_TEST_RETRY_INTERVAL = Duration.ofMillis(500L);

    private static final String USERNAME = DEFAULT_ADMIN_USER;
    @SuppressWarnings("squid:S2068")
    private static final String PASSWORD = DEFAULT_SYS_USER_PASSWORD;

    private final CertificateFingerprintExtractor logExtractor = new CertificateFingerprintExtractor();

    /**
     * Create a new Exasol Nano container with the default image {@code exasol/nano:latest}.
     */
    public ExasolNanoContainer() {
        this(EXASOL_NANO_DOCKER_IMAGE_REFERENCE);
    }

    /**
     * Create a new Exasol Nano container from a specific Docker image.
     *
     * @param dockerImageName name of the Docker image from which the container is created
     */
    public ExasolNanoContainer(final String dockerImageName) {
        super(DockerImageName.parse(dockerImageName));
        addExposedPorts(EXASOL_NANO_SQL_PORT, EXASOL_NANO_WEB_UI_PORT);
        withConnectTimeoutSeconds((int) CONNECTION_WAIT_TIMEOUT.toSeconds());
        withSharedMemorySize(EXASOL_NANO_SHARED_MEMORY_SIZE);
        withLogConsumer(logExtractor);
    }

    @Override
    public String getDriverClassName() {
        return JDBC_DRIVER_CLASS;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get the JDBC URL including the TLS certificate fingerprint.
     */
    @Override
    public String getJdbcUrl() {
        return String.format("jdbc:exa:%s:%d;fingerprint=%s;", getHost(), getMappedPort(EXASOL_NANO_SQL_PORT), getCertificateFingerprint());
    }

    /**
     * Get the TLS certificate fingerprint of the Exasol Nano container.
     * 
     * @return the TLS certificate fingerprint
     */
    public String getCertificateFingerprint() {
        return logExtractor.getCertificateFingerprint();
    }

    @Override
    public String getUsername() {
        return USERNAME;
    }

    @Override
    public String getPassword() {
        return PASSWORD;
    }

    /**
     * Create a JDBC connection using default username and password.
     *
     * @return database connection
     * @throws UncheckedSqlException if the connection cannot be established
     */
    public Connection createConnection() {
        return createConnectionForUser(getUsername(), getPassword());
    }

    /**
     * Create a JDBC connection for the given user.
     *
     * @param user     username
     * @param password password of the user
     * @return database connection
     * @throws UncheckedSqlException if the connection cannot be established
     */
    public Connection createConnectionForUser(final String user, final String password) {
        final Driver driver = getJdbcDriverInstance();
        final Properties info = new Properties();
        info.put("user", user);
        info.put("password", password);
        final String url = constructUrlForConnection("");
        try {
            return driver.connect(url, info);
        } catch (final SQLException exception) {
            throw new UncheckedSqlException(ExaError.messageBuilder("E-ETC-44")
                    .message("Failed to connect to {{jdbc url}}: {{error message}}", url, exception.getMessage())
                    .toString(), exception);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * The Exasol JDBC driver expects connection parameters to be separated by semicolons, e.g.
     * {@code jdbc:exa:localhost:8563;fingerprint=abc123}. This method ensures that the provided query string is
     * correctly appended to the base JDBC URL.
     * </p>
     */
    @Override
    protected String constructUrlForConnection(final String queryString) {
        final String baseUrl = getJdbcUrl();

        if ("".equals(queryString)) {
            return baseUrl;
        }

        if (!queryString.startsWith(";")) {
            throw new IllegalArgumentException("The ';' character must be included");
        }

        return baseUrl.endsWith(";")
                ? baseUrl + queryString.substring(1)
                : baseUrl + queryString;
    }

    @Override
    protected String getTestQueryString() {
        return "SELECT 1";
    }

    @Override
    protected void waitUntilContainerStarted() {
        logExtractor.waitForCertificateFingerprint(Duration.ofSeconds(30));
    }
}
