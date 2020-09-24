package com.exasol.containers;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static com.exasol.containers.ExasolContainerConstants.*;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import org.testcontainers.containers.*;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketFactory;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.ClusterConfiguration;
import com.exasol.containers.wait.strategy.BucketFsWaitStrategy;
import com.exasol.containers.wait.strategy.UdfContainerWaitStrategy;
import com.exasol.database.DatabaseService;
import com.exasol.database.DatabaseServiceFactory;
import com.exasol.exaconf.ConfigurationParser;
import com.exasol.exaoperation.ExaOperation;
import com.exasol.exaoperation.ExaOperationEmulator;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerNetwork;

// [external->dsn~testcontainer-framework-controls-docker-image-download~1]
// [impl->dsn~exasol-container-controls-docker-container~1]

@SuppressWarnings("squid:S2160") // Superclass adds state but does not override equals() and hashCode().
public class ExasolContainer<T extends ExasolContainer<T>> extends JdbcDatabaseContainer<T> {
    private static final long CONNECTION_WAIT_TIMEOUT_MILLISECONDS = 30000L;
    private static final long CONNECTION_TEST_RETRY_INTERVAL_MILLISECONDS = 100L;
    private ClusterConfiguration clusterConfiguration = null;
    // [impl->dsn~default-jdbc-connection-with-sys-credentials~1]
    private String username = ExasolContainerConstants.DEFAULT_ADMIN_USER;
    @SuppressWarnings("squid:S2068")
    private String password = ExasolContainerConstants.DEFAULT_SYS_USER_PASSWORD;
    private final LogPatternDetectorFactory detectorFactory;
    private Set<ExasolService> requiredServices = Set.of(ExasolService.values());
    private final Set<ExasolService> readyServices = new HashSet<>();
    private final ExaOperation exaOperation;
    private TimeZone timeZone;
    private boolean isReused = false;
    private final ExasolDockerImageReference dockerImageReference;

    /**
     * Create a new instance of an {@link ExasolContainer} from a specific docker image.
     *
     * @param dockerImageName name of the Docker image from which the container is created
     * @see ExasolDockerImageReference#parse(String) Examples for supported reference types
     */
    public ExasolContainer(final String dockerImageName) {
        this(ExasolDockerImageReference.parse(dockerImageName));
    }

    private ExasolContainer(final ExasolDockerImageReference dockerImageReference) {
        super(dockerImageReference.toString());
        this.dockerImageReference = dockerImageReference;
        this.detectorFactory = new LogPatternDetectorFactory(this);
        this.exaOperation = new ExaOperationEmulator(this);
    }

    /**
     * Create a new instance of an {@link ExasolContainer} with the default docker image.
     * <p>
     * Creates a container from the image defined in {@link ExasolContainerConstants#EXASOL_DOCKER_IMAGE_ID}. Note that
     * this is not necessarily the latest available version but rather a fixed version. That has the benefit, that tests
     * are guaranteed to be stable as long as you don't change the version of the Exasol test container dependency.
     */
    public ExasolContainer() {
        this(ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE);
    }

    /**
     * Configure the Exasol container.
     * <p>
     * Unless you provide different ports first, maps the following ports:
     * </p>
     * <ul>
     * <li>Default Database port</li>
     * <li>Default BucketFS port</li>
     * </ul>
     * <p>
     * Note that the ports must be exposed before the container is started. So while reading the information from the
     * cluster configuration would be more elegant, we don't have this option because the cluster configuration is not
     * available at this time.
     * </p>
     * <p>
     * Sets the container to privileged mode. This is needed for shared memory, huge-page support and other low-level
     * access.
     * </p>
     */
    // [impl->dsn~exasol-container-uses-privileged-mode~1]
    @Override
    protected void configure() {
        exposePorts();
        this.setPrivilegedMode(true);
        super.configure();
    }

    private static UnsupportedOperationException getTimeoutNotSupportedException() {
        throw new UnsupportedOperationException("The Exasol testcontainer do not support this configuration.");
    }

    /**
     * Get the default internal port of the database.
     * <p>
     * This method chooses the port number depending on the version of the Exasol database. This is necessary since the
     * port number was changed with version 7.
     * </p>
     * 
     * @return default internal port of the database
     */
    public int getDefaultInternalDatabasePort() {
        final int majorVersion = this.dockerImageReference.getMajorVersion()
                .orElseThrow(() -> new UnsupportedOperationException(
                        "Could not detect internal database port for custom image. Please specify the port explicitly using withExposedPorts()."));
        if (majorVersion >= 7) {
            return DEFAULT_CONTAINER_INTERNAL_DATABASE_PORT_V7_AND_ABOVE;
        } else {
            return DEFAULT_CONTAINER_INTERNAL_DATABASE_PORT;
        }
    }

    private void exposePorts() {
        if (this.getExposedPorts().isEmpty()) {
            this.addExposedPorts(getDefaultInternalDatabasePort(), getDefaultInternalBucketfsPort());
        }
        logger().debug("Exposing ports: {}", this.getExposedPorts());
    }

    @Override
    public Set<Integer> getLivenessCheckPortNumbers() {
        return Set.of(getMappedPort(getFirstDatabasePort()));
    }

    private int getFirstDatabasePort() {
        return this.clusterConfiguration.getDatabaseServiceConfiguration(0).getPort();
    }

    @Override
    public String getDriverClassName() {
        return JDBC_DRIVER_CLASS;
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:exa:" + getContainerIpAddress() + ":" + getMappedPort(getFirstDatabasePort());
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
     * Get the address-part of an Exasol-specific connection string.
     *
     * @return host (list) and port
     */
    public String getExaConnectionAddress() {
        return this.getContainerIpAddress() + ":" + getMappedPort(getFirstDatabasePort());
    }

    /**
     * Get the log pattern detector factory.
     *
     * @return log pattern detector factory
     */
    public LogPatternDetectorFactory getLogPatternDetectorFactory() {
        return this.detectorFactory;
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

    /**
     * Create a JDBC connection using default username and password.
     * 
     * @return database connection
     * @throws SQLException if the connection cannot be established
     */
    public Connection createConnection() throws SQLException {
        return createConnectionForUser(getUsername(), getPassword());
    }

    // [impl->dsn~exasol-container-ready-criteria~3]
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
     * Define which optional services you require.
     * <p>
     * Note that the JDBC service is always considered required, so you don't need to explicitly list it.
     * </p>
     *
     * @param optionalServices list of optional services you require
     * @return self reference for fluent programming
     */
    // [impl->dsn~defining-required-optional-service~1]
    public T withRequiredServices(final ExasolService... optionalServices) {
        final ExasolService[] services = new ExasolService[optionalServices.length + 1];
        services[0] = ExasolService.JDBC;
        for (int i = 0; i < optionalServices.length; ++i) {
            services[i + 1] = optionalServices[i];
        }
        this.requiredServices = Set.of(services);
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
        final BucketFactory manager = new BucketFactory(this.detectorFactory, getContainerIpAddress(),
                getClusterConfiguration(), getPortMappings());
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
        return getBucket(DEFAULT_BUCKETFS, DEFAULT_BUCKET);
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
    protected void containerIsStarting(final InspectContainerResponse containerInfo, final boolean reused) {
        super.containerIsStarting(containerInfo, reused);
        this.isReused = reused;
    }

    // [impl->dsn~exasol-container-ready-criteria~3]
    @Override
    protected void waitUntilContainerStarted() {
        waitUntilClusterConfigurationAvailable();
        final Instant afterUtc = Instant.now();
        waitUntilStatementCanBeExecuted();
        if (this.requiredServices.contains(ExasolService.BUCKETFS)) {
            if (!this.isReused) {
                new BucketFsWaitStrategy(this.detectorFactory, afterUtc).waitUntilReady(this);
            }
            this.readyServices.add(ExasolService.BUCKETFS);
        }
        if (this.requiredServices.contains(ExasolService.UDF)) {
            if (!this.isReused) {
                new UdfContainerWaitStrategy(this.detectorFactory, afterUtc).waitUntilReady(this);
            }
            this.readyServices.add(ExasolService.UDF);
        }
        logger().info("Exasol container started after waiting for the following services to become available: {}",
                this.requiredServices);
        if (this.isReused) {
            purgeDatabase();
        }
    }

    /**
     * 
     */
    // [impl->dsn~purging~1]
    public void purgeDatabase() {
        logger().info("Purging database for a clean setup");
        try (final Connection connection = createConnection();
                final Statement statement = connection.createStatement()) {
            new ExasolDatabaseCleaner(statement).purge();
        } catch (final SQLException exception) {
            throw new ExasolContainerInitializationException("Failed to purge database", exception);
        }
    }

    protected void waitUntilClusterConfigurationAvailable() {
        if (!this.isReused) {
            logger().debug("Waiting for cluster configuration to become available.");
            final WaitStrategy strategy = new LogMessageWaitStrategy().withRegEx(".*exadt:: setting hostname.*");
            strategy.waitUntilReady(this);
        }
        clusterConfigurationIsAvailable();
    }

    private void clusterConfigurationIsAvailable() {
        this.clusterConfiguration = readClusterConfiguration();
        this.timeZone = this.clusterConfiguration.getTimeZone();
        if (this.timeZone == null) {
            throw new IllegalStateException(
                    "Unable to get timezone from cluster configuration. Log entry detection does not work without TZ.");
        }
    }

    private ClusterConfiguration readClusterConfiguration() {
        try {
            logger().debug("Reading cluster configuration from \"{}\"", CLUSTER_CONFIGURATION_PATH);
            final Container.ExecResult result = execInContainer("cat", CLUSTER_CONFIGURATION_PATH);
            final String exaconf = result.getStdout();
            logger().debug(exaconf);
            return new ConfigurationParser(exaconf).parse();
        } catch (final UnsupportedOperationException | IOException exception) {
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

    private void waitUntilStatementCanBeExecuted() {
        sleepBeforeNextConnectionAttempt();
        final long beforeConnectionCheck = System.currentTimeMillis();
        while ((System.currentTimeMillis() - beforeConnectionCheck) < CONNECTION_WAIT_TIMEOUT_MILLISECONDS) {
            if (isConnectionAvailable()) {
                return;
            }
        }
        throw new ContainerLaunchException("Exasol container start-up timed out in connection test.");
    }

    private void sleepBeforeNextConnectionAttempt() {
        try {
            Thread.sleep(CONNECTION_TEST_RETRY_INTERVAL_MILLISECONDS);
        } catch (final InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new ContainerLaunchException("Container start-up wait was interrupted", interruptedException);
        }
    }

    private boolean isConnectionAvailable() {
        try (final Connection connection = createConnection("");
                final Statement statement = connection.createStatement();
                final ResultSet result = statement.executeQuery(getTestQueryString())) {
            if (result.next()) {
                return true;
            } else {
                throw new ContainerLaunchException("Startup check query failed. Exasol container start-up failed.");
            }
        } catch (final NoDriverFoundException exception) {
            throw new ContainerLaunchException(
                    "Unable to determine start status of container, because the referenced JDBC driver was not found.",
                    exception);
        } catch (final SQLException exception) {
            sleepBeforeNextConnectionAttempt();
        }
        return false;
    }

    /**
     * Get the IP address of the container <i>inside</i> the docker network.
     *
     * @return internal IP address
     */
    // [impl->dsn~ip-address-in-common-docker-network~1]
    @SuppressWarnings("squid:S2589") // getNetwork() can be NULL despite annotation that says otherwise
    public String getDockerNetworkInternalIpAddress() {
        final Network thisNetwork = getNetwork();
        if (thisNetwork != null) {
            final Map<String, ContainerNetwork> networks = getContainerInfo().getNetworkSettings().getNetworks();
            for (final ContainerNetwork network : networks.values()) {
                if (thisNetwork.getId().equals(network.getNetworkID())) {
                    return network.getIpAddress();
                }
            }
        }
        return "127.0.0.1";
    }

    /**
     * Check if a service is ready to be used.
     *
     * @param service service that is checked
     * @return {@code true} if the service is ready and can be used
     */
    public boolean isServiceReady(final ExasolService service) {
        return this.readyServices.contains(service);
    }

    /**
     * Get a database service.
     *
     * @param databaseName name of the database the service provides.
     * @return database service.
     */
    public DatabaseService getDatabaseService(final String databaseName) {
        return new DatabaseServiceFactory(this, getClusterConfiguration()).getDatabaseService(databaseName);
    }

    /**
     * Get a control object for EXAoperation.
     *
     * @return EXAoperation control object.
     */
    public ExaOperation getExaOperation() {
        return this.exaOperation;
    }

    /**
     * Get the time zone.
     *
     * @return time zone
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    // [impl->dsn~keep-container-running-if-reuse~1]
    @Override
    public void stop() {
        if (isShouldBeReused() && TestcontainersConfiguration.getInstance().environmentSupportsReuse()) {
            logger().warn(
                    "Leaving container running since reuse is enabled. Don't forget to stop and remove the container manually using docker rm -f CONTAINER_ID.");
        } else {
            super.stop();
        }
    }

    /**
     * Get the default internal port of the BucketFS.
     * <p>
     * This method chooses the BucketFS port number depending on the version of the Exasol database. This is necessary
     * since the port number was changed with version 7.
     * </p>
     *
     * @return default internal port of the database
     */
    public int getDefaultInternalBucketfsPort() {
        final int majorVersion = this.dockerImageReference.getMajorVersion()
                .orElseThrow(() -> new UnsupportedOperationException(
                        "Could not detect internal BucketFS port for custom image. Please specify the port explicitly using withExposedPorts()."));
        if (majorVersion >= 7) {
            return DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT_V7_AND_ABOVE;
        } else {
            return DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT;
        }
    }

    @Override
    public T withConnectTimeoutSeconds(final int connectTimeoutSeconds) {
        throw getTimeoutNotSupportedException();
    }

    @Override
    public T withStartupTimeout(final Duration startupTimeout) {
        throw getTimeoutNotSupportedException();
    }
}
