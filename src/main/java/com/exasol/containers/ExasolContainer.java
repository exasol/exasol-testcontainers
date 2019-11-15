package com.exasol.containers;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.JdbcDatabaseContainer;

import com.exasol.bucketfs.*;
import com.exasol.config.ClusterConfiguration;
import com.exasol.exaconf.ConfigurationParser;
import com.github.dockerjava.api.command.InspectContainerResponse;

// [external->dsn~testcontainer-framework-controls-docker-image-download~1]
// [impl->dsn~exasol-container-controls-docker-container~1]

@SuppressWarnings("squid:S2160") // Superclass adds state but does not override equals() and hashCode().
public class ExasolContainer<T extends ExasolContainer<T>> extends JdbcDatabaseContainer<T> {
    @SuppressWarnings("squid:S1075") // This is the default URI where EXAConf is supposed to be located.
    private static final String CLUSTER_CONFIGURATION_PATH = "/exa/etc/EXAConf";
    public static final String NAME = "exasol";
    private static final String JDBC_DRIVER_CLASS = "com.exasol.jdbc.EXADriver";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainer.class);
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
        this.addExposedPorts(ExasolContainerConstants.CONTAINER_INTERNAL_DATABASE_PORT, ExasolContainerConstants.CONTAINER_INTERNAL_BUCKETFS_PORT);
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
            LOGGER.info("Reading cluster configuration from \"{}\"", CLUSTER_CONFIGURATION_PATH);
            final Container.ExecResult result = execInContainer("cat", CLUSTER_CONFIGURATION_PATH);
            final String exaconf = result.getStdout();
            LOGGER.info(exaconf);
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
        return "jdbc:exa:" + getContainerIpAddress() + ":" + getMappedPort(ExasolContainerConstants.CONTAINER_INTERNAL_DATABASE_PORT);
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

    // [impl->dsn~exasol-container-ready-criteria~1]
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
}