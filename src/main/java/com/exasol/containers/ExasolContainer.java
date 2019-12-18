package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.*;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

import org.testcontainers.containers.*;

import com.exasol.bucketfs.*;
import com.exasol.config.ClusterConfiguration;
import com.exasol.containers.wait.LogFileEntryWaitStrategy;
import com.exasol.exaconf.ConfigurationParser;
import com.github.dockerjava.api.command.InspectContainerResponse;

// [external->dsn~testcontainer-framework-controls-docker-image-download~1]
// [impl->dsn~exasol-container-controls-docker-container~1]

@SuppressWarnings("squid:S2160") // Superclass adds state but does not override equals() and hashCode().
public class ExasolContainer<T extends ExasolContainer<T>> extends JdbcDatabaseContainer<T> {
    private static final String BUCKETFS_DAEMON_LOG_FILENAME_PATTERN = "bucketfsd.*.log";
    private static final String SCRIPT_LANGUGAGE_CONTAINER_READY_PATTERN = "ScriptLanguages.*extracted$";
    private ClusterConfiguration clusterConfiguration = null;
    // [impl->dsn~default-jdbc-connection-with-sys-credentials~1]
    private String username = ExasolContainerConstants.DEFAULT_ADMIN_USER;
    @SuppressWarnings("squid:S2068")
    private String password = ExasolContainerConstants.DEFAULT_SYS_USER_PASSWORD;

    public ExasolContainer(final String dockerImageName) {
        super(dockerImageName);
    }

    /**
     * Configure the Exasol container.
     * <p>
     * Maps the following ports:
     * </p>
     * <ul>
     * <li>Database port</li>
     * <li>BucketFS port</li>
     * </ul>
     * <p>
     * Sets the container to privileged mode. This is needed for shared memory, huge-page support and other low-level
     * access.
     * </p>
     */
    // [impl->dsn~exasol-container-uses-privileged-mode~1]
    @Override
    protected void configure() {
        this.addExposedPorts(ExasolContainerConstants.CONTAINER_INTERNAL_DATABASE_PORT,
                ExasolContainerConstants.CONTAINER_INTERNAL_BUCKETFS_PORT);
        this.setPrivilegedMode(true);
        super.configure();
    }

    @Override
    protected void containerIsStarted(final InspectContainerResponse containerInfo) {
        this.clusterConfiguration = readClusterConfiguration();
        super.containerIsStarted(containerInfo);
    }

    private ClusterConfiguration readClusterConfiguration() {
        try {
            logger().debug("Reading cluster configuration from \"{}\"", CLUSTER_CONFIGURATION_PATH);
            final Container.ExecResult result = execInContainer("cat", CLUSTER_CONFIGURATION_PATH);
            final String exaconf = result.getStdout();
            logger().debug(exaconf);
            return new ConfigurationParser(exaconf).parse();
        } catch (UnsupportedOperationException | IOException exception) {
            throw new ExasolContainerInitializationException(
                    "Unable to read cluster configuration from \"" + CLUSTER_CONFIGURATION_PATH + "\".", exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ExasolContainerInitializationException(
                    "Caught interrupt trying to read cluster configuration from \"" + CLUSTER_CONFIGURATION_PATH
                            + "\".",
                    exception);
        }
    }

    @Override
    public Set<Integer> getLivenessCheckPortNumbers() {
        return Set.of(getMappedPort(ExasolContainerConstants.CONTAINER_INTERNAL_DATABASE_PORT));
    }

    @Override
    public String getDriverClassName() {
        return JDBC_DRIVER_CLASS;
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:exa:" + getContainerIpAddress() + ":"
                + getMappedPort(ExasolContainerConstants.CONTAINER_INTERNAL_DATABASE_PORT);
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
     * @throws SQLException if the connection cannot be established
     */
    // [impl->dsn~exasol-container-provides-a-jdbc-connection-for-username-and-password~1]
    public Connection createConnectionForUser(final String user, final String password) throws SQLException {
        final Driver driver = getJdbcDriverInstance();
        final Properties info = new Properties();
        info.put("user", user);
        info.put("password", password);
        return driver.connect(constructUrlForConnection(""), info);
    }

    // [impl->dsn~exasol-container-ready-criteria~2]
    @Override
    protected String getTestQueryString() {
        return "SELECT 1 FROM DUAL";
    }

    // [dsn~exasol-container-provides-a-jdbc-connection-with-administrator-privileges~1]
    @Override
    public T withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public T withPassword(final String password) {
        this.password = password;
        return self();
    }

    /**
     * Get the cached Exasol cluster configuration.
     *
     * @return Exasol cluster configuration
     */
    public synchronized ClusterConfiguration getClusterConfiguration() {
        if (this.clusterConfiguration == null) {
            throw new IllegalStateException(
                    "Tried to access Exasol cluster configuration before it was read from the container.");
        }
        return this.clusterConfiguration;
    }

    /**
     * Get a bucket control object.
     *
     * @param bucketFsName name of the BucketFS filesystem the bucket belongs to
     * @param bucketName   name of the bucket
     * @return bucket control object
     */
    public Bucket getBucket(final String bucketFsName, final String bucketName) {
        final BucketFactory manager = new BucketFactory(getContainerIpAddress(), getClusterConfiguration(),
                getPortMappings());
        return manager.getBucket(bucketFsName, bucketName);
    }

    private Map<Integer, Integer> getPortMappings() {
        final Map<Integer, Integer> portMappings = new HashMap<>();
        for (final int exposedPort : getExposedPorts()) {
            portMappings.put(exposedPort, getMappedPort(exposedPort));
        }
        return portMappings;
    }

    /**
     * Get the default bucket (the one that in a standard installation always exists).
     *
     * @return default bucket control object
     */
    public Bucket getDefaultBucket() {
        return getBucket(BucketConstants.DEFAULT_BUCKETFS, BucketConstants.DEFAULT_BUCKET);
    }

    /**
     * Map the path of the Exasol cluster logs to a path on the host.
     * <p>
     * When the container is created with this option, then the cluster log directory is mapped to the given path on the
     * host. The logs are then created inside that directory and are accessible as normal files on the host for
     * debugging purposes.
     * </p>
     *
     * @param clusterLogsHostPath path on the host to which the directory for the cluster logs is mapped
     * @return {@code this} for fluent programming
     */
    // [impl->dsn~mapping-the-log-directory-to-the-host~1]
    public T withClusterLogsPath(final Path clusterLogsHostPath) {
        logger().debug("Mapping cluster log directory to host path: \"{}\"", clusterLogsHostPath);
        addFileSystemBind(clusterLogsHostPath.toString(), EXASOL_LOGS_PATH, BindMode.READ_WRITE);
        return self();
    }

    @Override
    protected void waitUntilContainerStarted() {
        super.waitUntilContainerStarted();
        waitUntilUdfLanguageContainerExtracted();
    }

    // [impl->dsn~exasol-container-ready-criteria~2]
    private void waitUntilUdfLanguageContainerExtracted() {
        logger().info("Waiting for UDF language container to be ready.");
        final LogFileEntryWaitStrategy strategy = new LogFileEntryWaitStrategy(this, EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, SCRIPT_LANGUGAGE_CONTAINER_READY_PATTERN);
        strategy.waitUntilReady(this);
        logger().info("UDF language container is ready.");
    }
}