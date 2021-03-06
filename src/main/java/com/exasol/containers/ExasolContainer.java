package com.exasol.containers;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static com.exasol.containers.ExasolContainerConstants.*;
import static com.exasol.containers.ExasolService.BUCKETFS;
import static com.exasol.containers.ExasolService.UDF;
import static com.exasol.containers.status.ServiceStatus.*;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.*;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketFactory;
import com.exasol.bucketfs.testcontainers.TestcontainerBucketFactory;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.ClusterConfiguration;
import com.exasol.containers.status.ContainerStatus;
import com.exasol.containers.status.ContainerStatusCache;
import com.exasol.containers.wait.strategy.BucketFsWaitStrategy;
import com.exasol.containers.wait.strategy.UdfContainerWaitStrategy;
import com.exasol.containers.workarounds.*;
import com.exasol.database.DatabaseService;
import com.exasol.database.DatabaseServiceFactory;
import com.exasol.dbcleaner.ExasolDatabaseCleaner;
import com.exasol.drivers.ExasolDriverManager;
import com.exasol.exaconf.ConfigurationParser;
import com.exasol.exaoperation.ExaOperation;
import com.exasol.exaoperation.ExaOperationEmulator;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerNetwork;

// [external->dsn~testcontainer-framework-controls-docker-image-download~1]
// [impl->dsn~exasol-container-controls-docker-container~1]

@SuppressWarnings("squid:S2160") // Superclass adds state but does not override equals() and hashCode().
public class ExasolContainer<T extends ExasolContainer<T>> extends JdbcDatabaseContainer<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainer.class);
    private static final long CONNECTION_TEST_RETRY_INTERVAL_MILLISECONDS = 100L;
    private ClusterConfiguration clusterConfiguration = null;
    // [impl->dsn~default-jdbc-connection-with-sys-credentials~1]
    private String username = ExasolContainerConstants.DEFAULT_ADMIN_USER;
    @SuppressWarnings("squid:S2068")
    private String password = ExasolContainerConstants.DEFAULT_SYS_USER_PASSWORD;
    private final LogPatternDetectorFactory detectorFactory;
    private Set<ExasolService> requiredServices = Set.of(ExasolService.values());
    private final ExaOperation exaOperation;
    private TimeZone timeZone;
    private boolean reused = false;
    private final ExasolDockerImageReference dockerImageReference;
    private boolean portAutodetectFailed = false;
    private int connectionWaitTimeoutSeconds = 200;
    private ExasolDriverManager driverManager = null;
    private final ContainerStatusCache statusCache = new ContainerStatusCache(
            Path.of(System.getProperty("java.io.tmpdir")));
    private ContainerStatus status = null;

    /**
     * Create a new instance of an {@link ExasolContainer} from a specific docker image.
     *
     * @param dockerImageName name of the Docker image from which the container is created
     * @see DockerImageReferenceFactory#parse(String) Examples for supported reference types
     */
    @SuppressWarnings("java:S1874") // This constructor is different from JdbcDatabaseContainer(String) and not
                                    // deprecated
    public ExasolContainer(final String dockerImageName) {
        this(dockerImageName, true);
    }

    /**
     * Create a new instance of an {@link ExasolContainer} from a specific docker image.
     *
     * @param dockerImageName    name of the Docker image from which the container is created
     * @param allowImageOverride set to {@code true} if you want to let users override the image via property
     * @see DockerImageReferenceFactory#parse(String) Examples for supported reference types
     */
    @SuppressWarnings("java:S1874") // This constructor is different from JdbcDatabaseContainer(String) and not
    // deprecated
    public ExasolContainer(final String dockerImageName, final boolean allowImageOverride) {
        this(DockerImageReferenceFactory
                .parse(allowImageOverride ? getOverridableDockerImageName(dockerImageName) : dockerImageName));
    }

    // [impl->dsn~override-docker-image-via-java-property~1]
    private static String getOverridableDockerImageName(final String dockerImageName) {
        return System.getProperty(DOCKER_IMAGE_OVERRIDE_PROPERTY, dockerImageName);
    }

    private ExasolContainer(final ExasolDockerImageReference dockerImageReference) {
        super(DockerImageName.parse(dockerImageReference.toString()));
        this.dockerImageReference = dockerImageReference;
        this.detectorFactory = new LogPatternDetectorFactory(this);
        this.exaOperation = new ExaOperationEmulator(this);
        try {
            addExposedPorts(getDefaultInternalDatabasePort());
            addExposedPorts(getDefaultInternalBucketfsPort());
        } catch (final PortDetectionException exception) {
            this.portAutodetectFailed = true;
        }
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
     * Get the docker image reference on which this container is based.
     *
     * @return docker image reference
     */
    public ExasolDockerImageReference getDockerImageReference() {
        return this.dockerImageReference;
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
        if (this.portAutodetectFailed) {
            if (getExposedPorts().isEmpty()) {
                throw new IllegalArgumentException(
                        "Could not detect internal ports for custom image. Please specify the port explicitly using withExposedPorts().");
            } else {
                LOGGER.warn("Could not detect internal ports for custom image. "
                        + "Don't forget to expose the database and BucketFs ports yourself.");
            }
        }
        LOGGER.debug("Exposing ports: {}", this.getExposedPorts());
        this.setPrivilegedMode(true);
        super.configure();
    }

    private static UnsupportedOperationException getTimeoutNotSupportedException() {
        throw new UnsupportedOperationException(
                "The Exasol testcontainer do not support this configuration. Use withJdbcConnectionTimeout instead.");
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
        if (this.dockerImageReference.hasMajor()) {
            if (this.dockerImageReference.getMajor() >= 7) {
                return DEFAULT_CONTAINER_INTERNAL_DATABASE_PORT_V7_AND_ABOVE;
            } else {
                return DEFAULT_CONTAINER_INTERNAL_DATABASE_PORT;
            }
        } else {
            throw (new PortDetectionException("database"));
        }
    }

    public static class PortDetectionException extends UnsupportedOperationException {
        private static final long serialVersionUID = -1871794026177194823L;

        public PortDetectionException(final String service) {
            super("Could not detect internal " + service + " port for custom image. "
                    + "Please specify the port explicitly using withExposedPorts().");
        }
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
     * Check if this is an existing container that is marked for reuse.
     *
     * @return {@code true} if this container is marked for reuse.
     */
    public boolean isReused() {
        return this.reused;
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
        final HashSet<ExasolService> services = new HashSet<>();
        addMandatoryServices(services);
        services.addAll(Arrays.asList(optionalServices));
        this.requiredServices = services;
        return self();
    }

    private void addMandatoryServices(final HashSet<ExasolService> services) {
        services.add(ExasolService.JDBC);
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
        final BucketFactory factory = new TestcontainerBucketFactory(this.detectorFactory, getContainerIpAddress(),
                getClusterConfiguration(), getPortMappings());
        return factory.getBucket(bucketFsName, bucketName);
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
        LOGGER.debug("Mapping cluster log directory to host path: \"{}\"", clusterLogsHostPath);
        addFileSystemBind(clusterLogsHostPath.toString(), EXASOL_LOGS_PATH, BindMode.READ_WRITE);
        return self();
    }

    @Override
    protected void containerIsStarting(final InspectContainerResponse containerInfo) {
        final String containerId = containerInfo.getId();
        if (this.statusCache.isCacheAvailable(containerId)) {
            this.status = this.statusCache.read(containerId);
        } else {
            LOGGER.debug("No status cache found for container \"{}\". Creating fresh status.", containerId);
            this.status = ContainerStatus.create(containerId);
        }
        super.containerIsStarting(containerInfo);
    }

    @Override
    protected void containerIsStarting(final InspectContainerResponse containerInfo, final boolean reused) {
        this.reused = reused;
        super.containerIsStarting(containerInfo, reused);
    }

    // [impl->dsn~exasol-container-ready-criteria~3]
    @Override
    protected void waitUntilContainerStarted() {
        waitUntilClusterConfigurationAvailable();
        final Instant afterUtc = Instant.now();
        waitUntilStatementCanBeExecuted();
        waitForBucketFs(afterUtc);
        waitForUdfContainer(afterUtc);
        LOGGER.info("Exasol container started after waiting for the following services to become available: {}",
                this.requiredServices);

    }

    private void waitForBucketFs(final Instant afterUtc) {
        if (isServiceReady(BUCKETFS)) {
            LOGGER.debug("BucketFS marked running in container status cache. Skipping startup monitoring.");
        } else {
            if (this.requiredServices.contains(BUCKETFS)) {
                this.status.setServiceStatus(BUCKETFS, NOT_READY);
                new BucketFsWaitStrategy(this.detectorFactory, afterUtc).waitUntilReady(this);
                this.status.setServiceStatus(BUCKETFS, READY);
            } else {
                this.status.setServiceStatus(BUCKETFS, NOT_CHECKED);
            }
        }
    }

    private void waitForUdfContainer(final Instant afterUtc) {
        if (isServiceReady(UDF)) {
            LOGGER.debug("UDF Containter marked running in container status cache. Skipping startup monitoring.");
        } else {
            if (this.requiredServices.contains(UDF)) {
                this.status.setServiceStatus(UDF, NOT_READY);
                new UdfContainerWaitStrategy(this.detectorFactory, afterUtc).waitUntilReady(this);
                this.status.setServiceStatus(UDF, READY);
            } else {

                this.status.setServiceStatus(UDF, NOT_CHECKED);
            }
        }
    }

    @Override
    protected void containerIsStarted(final InspectContainerResponse containerInfo, final boolean reused) {
        super.containerIsStarted(containerInfo, reused);
        applyWorkarounds();
        cleanUpDatabaseIfNecessary();
        cacheContainerStatus();
    }

    private void applyWorkarounds() {
        final LogRotationWorkaround logRotationWorkaround = new LogRotationWorkaround(this);
        try {
            final Set<String> previouslyAppliedWorkarounds = this.status.getAppliedWorkarounds();
            final WorkaroundManager manager = WorkaroundManager.create(previouslyAppliedWorkarounds,
                    logRotationWorkaround);
            final Set<String> appliedWorkaroundNames = manager.applyWorkarounds().stream().map(Workaround::getName)
                    .collect(Collectors.toUnmodifiableSet());
            this.status.addAllAppliedWorkarounds(appliedWorkaroundNames);
        } catch (final WorkaroundException exception) {
            throw new ExasolContainerInitializationException("Failed to apply necessary workarounds", exception);
        }
    }

    private void cleanUpDatabaseIfNecessary() {
        if (this.reused) {
            purgeDatabase();
        }
    }

    /**
     * Clean up the database.
     */
    // [impl->dsn~purging~1]
    public void purgeDatabase() {
        LOGGER.info("Purging database for a clean setup");
        try (final Connection connection = createConnection();
                final Statement statement = connection.createStatement()) {
            new ExasolDatabaseCleaner(statement).cleanDatabase();
        } catch (final SQLException exception) {
            throw new ExasolContainerInitializationException("Failed to purge database", exception);
        }
    }

    private void cacheContainerStatus() {
        this.statusCache.write(this.getContainerId(), this.status);
    }

    protected void waitUntilClusterConfigurationAvailable() {
        if (!this.reused) {
            LOGGER.debug("Waiting for cluster configuration to become available.");
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
            LOGGER.debug("Reading cluster configuration from \"{}\"", CLUSTER_CONFIGURATION_PATH);
            final Container.ExecResult result = execInContainer("cat", CLUSTER_CONFIGURATION_PATH);
            final String exaconf = result.getStdout();
            LOGGER.debug(exaconf);
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
        while ((System.currentTimeMillis() - beforeConnectionCheck) < (this.connectionWaitTimeoutSeconds * 1000L)) {
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
        return this.status.isServiceReady(service);
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
            LOGGER.warn("Leaving container running since reuse is enabled. " //
                    + "Don't forget to stop and remove the container manually using docker rm -f CONTAINER_ID.");
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
        if (this.dockerImageReference.hasMajor()) {
            if (this.dockerImageReference.getMajor() >= 7) {
                return DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT_V7_AND_ABOVE;
            } else {
                return DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT;
            }
        } else {
            throw new UnsupportedOperationException("Could not detect internal BucketFS port for custom image. " //
                    + "Please specify the port explicitly using withExposedPorts().");
        }
    }

    /**
     * Set the timeout for the JDBC connection.
     * <p>
     * This time is not measured after the startup, but after container internal cluster was started.
     * </p>
     *
     * @param timeoutInSeconds timeout in seconds.
     * @return self
     */
    public ExasolContainer<T> withJdbcConnectionTimeout(final int timeoutInSeconds) {
        this.connectionWaitTimeoutSeconds = timeoutInSeconds;
        return this;
    }

    /**
     * Get the JDBC connection timeout.
     *
     * @return JDBC connection timeout in seconds.
     */
    public int getJdbcConnectionTimeout() {
        return this.connectionWaitTimeoutSeconds;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated this method has no effect for the Exasol testcontainer. Use {@link #withJdbcConnectionTimeout(int)}
     *             instead
     */
    @Deprecated(since = "3.2.0")
    @Override
    @SuppressWarnings("java:S1133") // we need this method to hide the original one
    public T withConnectTimeoutSeconds(final int connectTimeoutSeconds) {
        throw getTimeoutNotSupportedException();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated this method has no effect for the Exasol testcontainer. Use {@link #withJdbcConnectionTimeout(int)}
     *             instead
     */
    @Deprecated(since = "3.2.0")
    @Override
    @SuppressWarnings("java:S1133") // we need this method to hide the original one
    public T withStartupTimeout(final Duration startupTimeout) {
        throw getTimeoutNotSupportedException();
    }

    /**
     * Get the manager for the installed drivers.
     *
     * @return driver manager
     */
    public final synchronized ExasolDriverManager getDriverManager() {
        if (this.driverManager == null) {
            this.driverManager = new ExasolDriverManager(getDefaultBucket());
        }
        return this.driverManager;
    }

    /**
     * Get the IP address of the host running this container.
     *
     * @return IP address of the host
     */
    public String getHostIp() {
        final HostIpDetector detector = new HostIpDetector(this);
        return detector.getHostIp();
    }
}
