package com.exasol.containers.nano;

import static com.exasol.containers.ExasolContainerConstants.*;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolNanoContainer.class);

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

    private static final Duration CONNECTION_TEST_RETRY_INTERVAL = Duration.ofMillis(500L);

    private String username = DEFAULT_ADMIN_USER;
    @SuppressWarnings("squid:S2068")
    private String password = DEFAULT_SYS_USER_PASSWORD;
    /** Timeout for JDBC connection readiness checks. */
    private Duration connectionWaitTimeout = Duration.ofSeconds(60);
    /** Parameter {@code logintimeout} for the JDBC driver. */
    private Duration jdbcLoginTimeout = Duration.ofSeconds(10);

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
        withSharedMemorySize(EXASOL_NANO_SHARED_MEMORY_SIZE);
    }

    @Override
    public String getDriverClassName() {
        return JDBC_DRIVER_CLASS;
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:exa:" + getHost() + ":" + getFirstMappedDatabasePort() + ";validateservercertificate=0;"
                + getCommonJdbcParameters();
    }

    private String getCommonJdbcParameters() {
        if (this.jdbcLoginTimeout != null) {
            return String.format("logintimeout=%d;", this.jdbcLoginTimeout.toMillis());
        } else {
            return "";
        }
    }

    /**
     * Get the mapped SQL endpoint port.
     *
     * @return mapped SQL endpoint port
     */
    public Integer getFirstMappedDatabasePort() {
        return getMappedPort(EXASOL_NANO_SQL_PORT);
    }

    /**
     * Get the address-part of an Exasol-specific connection string.
     *
     * @return host and port
     */
    public String getExaConnectionAddress() {
        return getHost() + ":" + getFirstMappedDatabasePort();
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Create a JDBC connection for the given user.
     *
     * @param user     username
     * @param password password of the user
     * @return database connection
     * @throws UncheckedSqlException if the connection cannot be established
     */
    public Connection createConnectionForUser(final String user, final String password) throws UncheckedSqlException {
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
     * Create a JDBC connection using default username and password.
     *
     * @return database connection
     * @throws UncheckedSqlException if the connection cannot be established
     */
    public Connection createConnection() throws UncheckedSqlException {
        return createConnectionForUser(getUsername(), getPassword());
    }

    @Override
    protected String getTestQueryString() {
        return "SELECT 1";
    }

    @Override
    public ExasolNanoContainer withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public ExasolNanoContainer withPassword(final String password) {
        this.password = password;
        return self();
    }

    /**
     * Set the timeout for the JDBC readiness check.
     *
     * @param timeout timeout for the JDBC readiness check
     * @return self
     */
    public ExasolNanoContainer withJdbcConnectionTimeout(final Duration timeout) {
        this.connectionWaitTimeout = timeout;
        return self();
    }

    /**
     * Set parameter {@code logintimeout} for the JDBC driver. Default: 10 seconds.
     *
     * @param timeout login timeout parameter for the JDBC driver, or {@code null} to omit it
     * @return self
     */
    public ExasolNanoContainer withJdbcLoginTimeout(final Duration timeout) {
        this.jdbcLoginTimeout = timeout;
        return self();
    }

    @Override
    public Set<Integer> getLivenessCheckPortNumbers() {
        return Set.of(getFirstMappedDatabasePort());
    }

    @Override
    protected void waitUntilContainerStarted() {
        new ConnectionWaiter(this, this.connectionWaitTimeout).waitUntilStatementCanBeExecuted();
    }

    private static final class ConnectionWaiter {
        private final ExasolNanoContainer container;
        private final Duration timeout;
        private SQLException lastConnectionException;

        private ConnectionWaiter(final ExasolNanoContainer container, final Duration timeout) {
            this.container = container;
            this.timeout = timeout;
        }

        private void waitUntilStatementCanBeExecuted() {
            LOGGER.trace("Waiting {} for JDBC connection", this.timeout);
            sleepBeforeNextConnectionAttempt();
            final Instant before = Instant.now();
            final Instant expiry = before.plus(this.timeout);
            while (Instant.now().isBefore(expiry)) {
                if (isConnectionAvailable()) {
                    LOGGER.trace("Connection succeeded after {}", Duration.between(before, Instant.now()));
                    return;
                }
            }
            final Duration timeoutAfter = Duration.between(before, Instant.now());
            throw new ContainerLaunchException(ExaError.messageBuilder("F-ETC-45")
                    .message("Exasol Nano container start-up timed out trying connection to {{url}} using query {{query}}"
                            + " after {{after}} seconds. Last connection exception was: {{exception}}")
                    .parameter("url", this.container.getJdbcUrl(),
                            "JDBC URL of the connection to the Exasol Nano Testcontainer")
                    .parameter("query", this.container.getTestQueryString(), "Query used to test the connection")
                    .parameter("after", timeoutAfter.toSeconds())
                    .parameter("exception",
                            (this.lastConnectionException == null) ? "none" : this.lastConnectionException.getMessage(),
                            "exception thrown on last connection attempt")
                    .toString(), this.lastConnectionException);
        }

        private void sleepBeforeNextConnectionAttempt() {
            try {
                Thread.sleep(CONNECTION_TEST_RETRY_INTERVAL.toMillis());
            } catch (final InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                throw new ContainerLaunchException("Container start-up wait was interrupted", interruptedException);
            }
        }

        private boolean isConnectionAvailable() {
            try (final Connection connection = this.container.createConnection("");
                    final Statement statement = connection.createStatement();
                    final ResultSet result = statement.executeQuery(this.container.getTestQueryString())) {
                if (result.next()) {
                    return true;
                } else {
                    throw new ContainerLaunchException(
                            "Startup check query failed. Exasol Nano container start-up failed.");
                }
            } catch (final NoDriverFoundException exception) {
                throw new ContainerLaunchException(ExaError.messageBuilder("E-ETC-46").message(
                        "Unable to determine start status of container, because the referenced JDBC driver was not found: {{cause}}",
                        exception.getMessage()).toString(), exception);
            } catch (final SQLException exception) {
                this.lastConnectionException = exception;
                sleepBeforeNextConnectionAttempt();
            }
            return false;
        }
    }
}
