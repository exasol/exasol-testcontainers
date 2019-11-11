package com.exasol.containers;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.JdbcDatabaseContainer;

import com.exasol.bucketfs.*;
import com.exasol.config.ClusterConfiguration;
import com.exasol.exaconf.ConfigurationParser;
import com.github.dockerjava.api.command.InspectContainerResponse;

@SuppressWarnings("squid:S2160") // Superclass adds state but does not override equals() and hashCode().
public class ExasolContainer<T extends ExasolContainer<T>> extends JdbcDatabaseContainer<T> {
    @SuppressWarnings("squid:S1075") // This is the default URI where EXAConf is supposed to be located.
    private static final String CLUSTER_CONFIGURATION_PATH = "/exa/etc/EXAConf";
    public static final String NAME = "exasol";
    private static final int CONTAINER_INTERNAL_DATABASE_PORT = 8888;
    private static final int CONTAINER_INTERNAL_BUCKETFS_PORT = 6583;
    private static final String JDBC_DRIVER_CLASS = "com.exasol.jdbc.EXADriver";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainer.class);
    private String username = "SYS";
    private ClusterConfiguration clusterConfiguration = null;
    // The following assignment intentionally contains the initial password for the database administrator.
    // Keep in mind that this project deals with disposable containers that should only be used in integration tests.
    @SuppressWarnings("squid:S2068")
    private String password = "EXASOL";

    public ExasolContainer(final String dockerImageName) {
        super(dockerImageName);
    }

    /**
     * Configure the Exasol container.
     * <p>
     * Maps the following ports:
     * <ul>
     * <li>Database port</li>
     * <li>BucketFS port</li>
     * </ul>
     * </p>
     * <p>
     * Sets the container to privileged mode. This is needed for shared memory, huge-page support and other low-level
     * access.
     */
    @Override
    protected void configure() {
        this.addExposedPorts(CONTAINER_INTERNAL_DATABASE_PORT, CONTAINER_INTERNAL_BUCKETFS_PORT);
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
        return Set.of(getMappedPort(CONTAINER_INTERNAL_DATABASE_PORT));
    }

    @Override
    public String getDriverClassName() {
        return JDBC_DRIVER_CLASS;
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:exa:" + getContainerIpAddress() + ":" + getMappedPort(CONTAINER_INTERNAL_DATABASE_PORT);
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    protected String getTestQueryString() {
        return "SELECT 1 FROM DUAL";
    }

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