package com.exasol.containers;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static com.exasol.containers.ExasolContainerConstants.*;
import static com.exasol.containers.ExasolService.BUCKETFS;
import static com.exasol.containers.ExasolService.UDF;
import static com.exasol.containers.ExitType.EXIT_ERROR;
import static com.exasol.containers.ExitType.EXIT_SUCCESS;
import static com.exasol.containers.exec.ExitCode.OK;
import static com.exasol.containers.status.ServiceStatus.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
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
import org.testcontainers.utility.*;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor.FilterStrategy;
import com.exasol.bucketfs.testcontainers.TestcontainerBucketFactory;
import com.exasol.bucketfs.testcontainers.TestcontainerBucketFactory.Builder;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.ClusterConfiguration;
import com.exasol.containers.slc.ScriptLanguageContainer;
import com.exasol.containers.slc.ScriptLanguageContainerInstaller;
import com.exasol.containers.ssh.*;
import com.exasol.containers.status.ContainerStatus;
import com.exasol.containers.status.ContainerStatusCache;
import com.exasol.containers.tls.CertificateProvider;
import com.exasol.containers.wait.strategy.BucketFsWaitStrategy;
import com.exasol.containers.wait.strategy.UdfContainerWaitStrategy;
import com.exasol.containers.workarounds.*;
import com.exasol.database.DatabaseService;
import com.exasol.database.DatabaseServiceFactory;
import com.exasol.dbcleaner.ExasolDatabaseCleaner;
import com.exasol.drivers.ExasolDriverManager;
import com.exasol.errorreporting.ExaError;
import com.exasol.exaconf.ConfigurationParser;
import com.exasol.exaoperation.ExaOperation;
import com.exasol.exaoperation.ExaOperationEmulator;
import com.exasol.support.SupportInformationRetriever;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.jcraft.jsch.JSchException;

/**
 * Exasol-specific extension of the {@link JdbcDatabaseContainer} concept.
 * <p>
 * Adds fine-grained service readiness checks, BucketFS access, driver management and a lot more Exasol-specific
 * functions on top of basic JDBC connection support.
 * </p>
 *
 * @param <T> container type self reference
 */
// [external->dsn~testcontainer-framework-controls-docker-image-download~1]
// [impl->dsn~exasol-container-controls-docker-container~1]
@SuppressWarnings("squid:S2160") // Superclass adds state but does not override equals() and hashCode().
public class ExasolContainer<T extends ExasolContainer<T>> extends JdbcDatabaseContainer<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainer.class);
    private static final long CONNECTION_TEST_RETRY_INTERVAL_MILLISECONDS = 500L;
    private ClusterConfiguration clusterConfiguration = null;
    // [impl->dsn~default-jdbc-connection-with-sys-credentials~1]
    private String username = ExasolContainerConstants.DEFAULT_ADMIN_USER;
    @SuppressWarnings("squid:S2068")
    private String password = ExasolContainerConstants.DEFAULT_SYS_USER_PASSWORD;
    private final LogPatternDetectorFactory detectorFactory;
    private Set<ExasolService> requiredServices = Set.of(ExasolService.values());
    private final ExaOperation exaOperation;
    private final CertificateProvider certificateProvider;
    private TimeZone timeZone;
    private boolean reused = false;
    private final ExasolDockerImageReference dockerImageReference;
    private boolean portAutodetectFailed = false;
    /** Timeout for JDBC connection. Typically this takes around 40s. */
    private Duration connectionWaitTimeout = Duration.ofSeconds(250);
    private ExasolDriverManager driverManager = null;
    private final ContainerStatusCache statusCache = new ContainerStatusCache(CACHE_DIRECTORY);
    private ContainerStatus status = null;
    private SupportInformationRetriever supportInformationRetriever = null;
    private boolean errorWhileWaitingForServices = false;
    private SQLException lastConnectionException = null;
    private Path temporaryCredentialsDirectory = CACHE_DIRECTORY;
    private DockerAccess dockerAccess = null;
    private final List<ScriptLanguageContainer> scriptLanguageContainers = new ArrayList<>();

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
    // [impl->dsn~override-docker-image-via-java-property~1]
    public ExasolContainer(final String dockerImageName, final boolean allowImageOverride) {
        this(DockerImageReferenceFactory.parseOverridable(dockerImageName, allowImageOverride));
        this.supportInformationRetriever = new SupportInformationRetriever(this);
    }

    private ExasolContainer(final ExasolDockerImageReference dockerImageReference) {
        super(DockerImageName.parse(dockerImageReference.toString()));
        this.dockerImageReference = dockerImageReference;
        this.detectorFactory = new LogPatternDetectorFactory(this);
        this.exaOperation = new ExaOperationEmulator(this);
        final ContainerFileOperations containerFileOperations = new ContainerFileOperations(this);
        this.certificateProvider = new CertificateProvider(this::getOptionalClusterConfiguration,
                containerFileOperations);
        try {
            addExposedPorts(getDefaultInternalDatabasePort());
            addExposedPorts(getDefaultInternalBucketfsPort());
            addExposedPorts(getDefaultInternalRpcPort());
            addExposedPorts(SSH_PORT);
        } catch (final PortDetectionException exception) {
            LOGGER.warn("Failed to detect internal ports.", exception);
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
        this(ExasolContainerConstants.EXASOL_DOCKER_IMAGE_VERSION);
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
        configureExposedPorts();
        configurePrivilegedMode();
        this.dockerAccess = createDockerAccess();
        copyAuthorizedKeys(this.dockerAccess);
        super.configure();
    }

    private void configureExposedPorts() {
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
    }

    private void configurePrivilegedMode() {
        this.setPrivilegedMode(true);
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

    @Override
    public Set<Integer> getLivenessCheckPortNumbers() {
        return Set.of(getFirstMappedDatabasePort());
    }

    private int getFirstDatabasePort() {
        return this.getClusterConfiguration().getDatabaseServiceConfiguration(0).getPort();
    }

    @Override
    public String getDriverClassName() {
        return JDBC_DRIVER_CLASS;
    }

    @Override
    public String getJdbcUrl() {
        final Optional<String> fingerprint = this.getTlsCertificateFingerprint();
        if ((this.clusterConfiguration != null) && (getDockerImageReference().getMajor() >= 7)
                && fingerprint.isPresent()) {
            return getJdbcUrlWithFingerprint(fingerprint.get());
        } else {
            return getJdbcUrlWithoutFingerprint();
        }
    }

    private String getJdbcUrlWithFingerprint(final String fingerprint) {
        return "jdbc:exa:" + getHost() + ":" + getFirstMappedDatabasePort()
                + ";validateservercertificate=1;fingerprint=" + fingerprint + ";";
    }

    private String getJdbcUrlWithoutFingerprint() {
        return "jdbc:exa:" + getHost() + ":" + getFirstMappedDatabasePort() + ";validateservercertificate=0" + ";";
    }

    /**
     * Get the mapped URL of the RPC interface.
     *
     * @return mapped URL of the RPC interface
     */
    public String getRpcUrl() {
        return "https://" + getHost() + ":" + getMappedPort(getDefaultInternalRpcPort()) + "/jrpc";
    }

    /**
     * Get the port to which the first database port in the Exasol configuration is mapped.
     *
     * @return mapped first database port
     */
    public Integer getFirstMappedDatabasePort() {
        return getMappedPort(getFirstDatabasePort());
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
        return this.getHost() + ":" + getFirstMappedDatabasePort();
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
     * @throws UncheckedSqlException if the connection cannot be established
     */
    // [impl->dsn~exasol-container-provides-a-jdbc-connection-for-username-and-password~1]
    public Connection createConnectionForUser(final String user, final String password) throws UncheckedSqlException {
        final Driver driver = getJdbcDriverInstance();
        final Properties info = new Properties();
        info.put("user", user);
        info.put("password", password);
        final String url = constructUrlForConnection("");
        try {
            return driver.connect(url, info);
        } catch (final SQLException exception) {
            throw new UncheckedSqlException(ExaError.messageBuilder("E-ETC-26")
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

    // [impl->dsn~exasol-container-ready-criteria~3]
    @Override
    protected String getTestQueryString() {
        return "SELECT 1";
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

    @Override
    public T withReuse(final boolean reuse) {
        final ExasolDockerImageReference image = getDockerImageReference();
        if (!image.hasMajor()) {
            LOGGER.info("Docker instance reuse requested, but could not detect Exasol major version of docker image {}."
                    + " Using normal mode instead.", image);
            return super.withReuse(false);
        }
        if (image.getMajor() < 7) {
            LOGGER.info("Docker instance reuse requested, but this is not supported by Exasol version below 7."
                    + " Using normal mode instead.");
            return super.withReuse(false);
        } else {
            return super.withReuse(reuse);
        }
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

    /**
     * Define the path where the Exasol container should store temporary credentials.
     * <p>
     * This defaults to {@code System.getProperty("java.io.tmpdir") + "/exasol_testcontainers/"}.
     * 
     * @param path path where the container stores temporary credentials
     * @return self reference for fluent programming
     */
    // [impl->dsn~configuring-the-directory-for-temporary-credentials~1]
    public T withTemporaryCredentialsDirectory(final Path path) {
        this.temporaryCredentialsDirectory = path;
        return self();
    }

    /**
     * Install an additional custom Script Language Container (SLC).
     * 
     * @param scriptLanguageContainer definition for the SLC to install
     * @return self reference for fluent programming
     */
    public T withScriptLanguageContainer(final ScriptLanguageContainer scriptLanguageContainer) {
        this.scriptLanguageContainers.add(scriptLanguageContainer);
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
            throw new IllegalStateException(ExaError.messageBuilder("E-ETC-25")
                    .message("Tried to access Exasol cluster configuration before it was read from the container.")
                    .mitigation("Wait until startup is complete.").toString());
        }
        return this.clusterConfiguration;
    }

    private Optional<ClusterConfiguration> getOptionalClusterConfiguration() {
        return Optional.ofNullable(this.clusterConfiguration);
    }

    /**
     * Get a bucket control object with default filter strategy {@link FilterStrategy} {@code TIME_STAMP}.
     *
     * @param bucketFsName name of the BucketFS file system the bucket belongs to
     * @param bucketName   name of the bucket
     * @return bucket control object
     */
    public Bucket getBucket(final String bucketFsName, final String bucketName) {
        return getBucket(bucketFsName, bucketName, FilterStrategy.TIME_STAMP);
    }

    /**
     * Get a bucket control object.
     *
     * @param bucketFsName   name of the BucketFS file system the bucket belongs to
     * @param bucketName     name of the bucket
     * @param filterStrategy filter strategy to use for rejecting irrelevant entries in log file
     * @return bucket control object
     */
    public Bucket getBucket(final String bucketFsName, final String bucketName, final FilterStrategy filterStrategy) {
        final Builder builder = TestcontainerBucketFactory.builder() //
                .host(getHost()) //
                .clusterConfiguration(getClusterConfiguration()) //
                .portMappings(getPortMappings()) //
                .detectorFactory(this.detectorFactory) //
                .filterStrategy(filterStrategy);
        getTlsCertificate().ifPresent(builder::certificate);
        return builder //
                .build() //
                .getBucket(bucketFsName, bucketName);
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

    @Override
    public void start() {
        checkScriptLanguageContainersRequiresExposedBucketFSPort();
        super.start();
        checkClusterConfigurationForMinimumSupportedDBVersion();
        ContainerSynchronizationVerifier.create(ContainerTimeService.create(this)).verifyClocksInSync();
    }

    // [impl->dsn~exasol-container-ready-criteria~3]
    @Override
    protected void waitUntilContainerStarted() {
        try {
            waitUntilClusterConfigurationAvailable();
            waitUntilStatementCanBeExecuted();
            waitForBucketFs();
            waitForUdfContainer();
            LOGGER.info("Exasol container started after waiting for the following services to become available: {}",
                    this.requiredServices);
        } catch (final Exception exception) {
            this.errorWhileWaitingForServices = true;
            throw exception;
        }
    }

    /**
     * Wait for BucketFS to become operational.
     */
    protected void waitForBucketFs() {
        if (isServiceReady(BUCKETFS)) {
            LOGGER.debug("BucketFS marked running in container status cache. Skipping startup monitoring.");
        } else {
            if (this.requiredServices.contains(BUCKETFS)) {
                this.status.setServiceStatus(BUCKETFS, NOT_READY);
                new BucketFsWaitStrategy(this.detectorFactory).waitUntilReady(this);
                this.status.setServiceStatus(BUCKETFS, READY);
            } else {
                this.status.setServiceStatus(BUCKETFS, NOT_CHECKED);
            }
        }
    }

    /**
     * Wait until the UDF container is available.
     */
    protected void waitForUdfContainer() {
        if (isServiceReady(UDF)) {
            LOGGER.debug("UDF Container marked running in container status cache. Skipping startup monitoring.");
            return;
        }
        if (!this.requiredServices.contains(UDF)) {
            this.status.setServiceStatus(UDF, NOT_CHECKED);
            return;
        }
        if (languageContainersExtracted()) {
            this.status.setServiceStatus(UDF, READY);
            return;
        }

        this.status.setServiceStatus(UDF, NOT_READY);
        new UdfContainerWaitStrategy(this.detectorFactory).waitUntilReady(this);
        this.status.setServiceStatus(UDF, READY);
    }

    /**
     * @return {@code true} if language containers are extracted by default. In Exasol database version 8 and above this
     *         is the case and the container does not need to wait for UDF service to get ready.
     */
    private boolean languageContainersExtracted() {
        return this.dockerImageReference.hasMajor() && (this.dockerImageReference.getMajor() >= 8);
    }

    @Override
    protected void containerIsStarted(final InspectContainerResponse containerInfo, final boolean reused) {
        super.containerIsStarted(containerInfo, reused);
        applyWorkarounds();
        cleanUpDatabaseIfNecessary();
        installSlcIfNecessary();
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

    // [impl->dsn~install-custom-slc.only-if-required~1]
    private void installSlcIfNecessary() {
        if (this.scriptLanguageContainers.isEmpty()) {
            return;
        }

        final ScriptLanguageContainerInstaller slcManager = ScriptLanguageContainerInstaller.create(this);
        for (final ScriptLanguageContainer slc : this.scriptLanguageContainers) {
            if (this.status.isInstalled(slc)) {
                LOGGER.debug("SLC {} already installed. Skipping installation.", slc);
            } else {
                slcManager.install(slc);
                this.status.addInstalledSlc(slc);
            }
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

    /**
     * Wait until we can read from the Exasol cluster configuration.
     */
    protected void waitUntilClusterConfigurationAvailable() {
        if (!this.reused) {
            final Duration timeout = Duration.ofSeconds(60);
            final Instant start = Instant.now();
            LOGGER.trace("Waiting {} for cluster configuration to become available.", timeout);
            final WaitStrategy strategy = new LogMessageWaitStrategy().withRegEx(".*exadt:: setting hostname.*")
                    .withStartupTimeout(timeout);
            strategy.waitUntilReady(this);
            LOGGER.trace("Cluster configuration available after {}.", Duration.between(start, Instant.now()));
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

    private void checkClusterConfigurationForMinimumSupportedDBVersion() {
        final String dbVersion = this.clusterConfiguration.getDBVersion();
        DBVersionChecker.minimumSupportedDbVersionCheck(dbVersion);
    }

    private void checkScriptLanguageContainersRequiresExposedBucketFSPort() {
        final int bucketfsPort = getDefaultInternalBucketfsPort();
        if (!scriptLanguageContainers.isEmpty() && !getExposedPorts().contains(bucketfsPort)) {
            throw new ContainerLaunchException(ExaError.messageBuilder("E-ETC-43")
                    .message("Installation of ScriptLanguageContainer requires the BucketFS port {{port}} " +
                            "to be exposed", bucketfsPort).toString());
        }
    }

    /**
     * Copy local file to container.
     *
     * @param local  path to local file
     * @param remote path to remote file
     * @throws SshException on errors during write operation
     */
    public void copyFileToContainer(final Path local, final String remote) throws SshException {
        if (this.dockerAccess.supportsDockerExec()) {
            super.copyFileToContainer(MountableFile.forHostPath(local), remote);
            return;
        }

        try {
            this.dockerAccess.getSsh().writeRemoteFile(local, remote);
        } catch (final JSchException | IOException exception) {
            throw new SshException(ExaError.messageBuilder("E-ETC-21") //
                    .message("Failed to copy local file {{local}} to target path {{remote}} in container.", //
                            local, remote)
                    .toString(), exception);
        }
    }

    private ClusterConfiguration readClusterConfiguration() {
        try {
            final Container.ExecResult result = execInContainer("cat", CLUSTER_CONFIGURATION_PATH);
            final String exaconf = result.getStdout();
            return new ConfigurationParser(exaconf).parse();
        } catch (final UnsupportedOperationException | IOException | SshException exception) {
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
        LOGGER.trace("Waiting {} for JDBC connection", this.connectionWaitTimeout);
        sleepBeforeNextConnectionAttempt();
        final Instant before = Instant.now();
        final Instant expiry = before.plus(this.connectionWaitTimeout);
        while (Instant.now().isBefore(expiry)) {
            if (isConnectionAvailable()) {
                LOGGER.trace("Connection succeeded after {}", Duration.between(before, Instant.now()));
                return;
            }
        }
        final Duration timeoutAfter = Duration.between(before, Instant.now());
        throw new ContainerLaunchException(ExaError.messageBuilder("F-ETC-5")
                .message("Exasol container start-up timed out trying connection to {{url}} using query {{query}}"
                        + " after {{after}} seconds. Last connection exception was: {{exception}}")
                .parameter("url", getJdbcUrl(), "JDBC URL of the connection to the Exasol Testcontainer")
                .parameter("query", getTestQueryString(), "Query used to test the connection")
                .parameter("after", timeoutAfter.toSeconds())
                .parameter("exception",
                        (this.lastConnectionException == null) ? "none" : this.lastConnectionException.getMessage(),
                        "exception thrown on last connection attempt")
                .toString(), this.lastConnectionException);
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
            throw new ContainerLaunchException(ExaError.messageBuilder("E-ETC-24").message(
                    "Unable to determine start status of container, because the referenced JDBC driver was not found: {{cause}}",
                    exception.getMessage()).toString(), exception);
        } catch (final SQLException exception) {
            this.lastConnectionException = exception;
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

    @Override
    public Container.ExecResult execInContainer(final Charset charset, final String... command)
            throws UnsupportedOperationException, IOException, InterruptedException {
        if (this.dockerAccess.supportsDockerExec()) {
            return super.execInContainer(charset, command);
        } else {
            return this.dockerAccess.getSsh().execute(charset, command);
        }
    }

    @Override
    protected void containerIsStopping(final InspectContainerResponse containerInfo) {
        final ContainerState state = containerInfo.getState();
        final ExitType exitType = (this.errorWhileWaitingForServices || state.getOOMKilled()
                || (state.getExitCodeLong() != OK) || state.getDead()) ? EXIT_ERROR //
                        : EXIT_SUCCESS;
        collectSupportInformation(exitType);
        super.containerIsStopping(containerInfo);
    }

    // [impl->dsn~keep-container-running-if-reuse~1]
    @Override
    public void stop() {
        if (isShouldBeReused() && TestcontainersConfiguration.getInstance().environmentSupportsReuse()) {
            LOGGER.info("Leaving container running since reuse is enabled. " //
                    + "Don't forget to stop and remove the container manually using docker rm -f CONTAINER_ID.");
            collectSupportInformation(EXIT_SUCCESS);
        } else {
            super.stop();
        }
    }

    private void collectSupportInformation(final ExitType exitType) {
        if (this.dockerImageReference.hasMajor() && (this.dockerImageReference.getMajor() >= 7)) {
            this.supportInformationRetriever.run(exitType);
        } else {
            LOGGER.info("Skipping support information retrieval for version {}, only supported with version >= 7",
                    this.dockerImageReference);
        }
    }

    /**
     * Get the default internal port of the BucketFS.
     * <p>
     * This method chooses the BucketFS port number depending on the version of the Exasol database. This is necessary
     * since the port number was changed with version 7.
     * </p>
     *
     * @return default internal port of the BucketFS
     */
    public int getDefaultInternalBucketfsPort() {
        if (this.dockerImageReference.hasMajor() && this.dockerImageReference.hasMinor()
                && this.dockerImageReference.hasFix()) {
            if (this.dockerImageReference.getMajor() > 8
                    || (this.dockerImageReference.getMajor() == 8 && this.dockerImageReference.getMinor() >= 29)) {
                return DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT_V8_29_AND_ABOVE;
            }
            if (this.dockerImageReference.getMajor() >= 7) {
                return DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT_V7_AND_ABOVE;
            } else {
                return DEFAULT_CONTAINER_INTERNAL_BUCKETFS_PORT;
            }
        } else {
            throw new UnsupportedOperationException(
                    "Could not detect internal BucketFS port for custom image '" + this.dockerImageReference + "'. " //
                            + "Please specify the port explicitly using withExposedPorts().");
        }
    }

    /**
     * Get the default internal port of the RPC interface.
     *
     * @return default internal port of the RPC interface
     */
    public int getDefaultInternalRpcPort() {
        return DEFAULT_CONTAINER_INTERNAL_RPC_PORT;
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
        this.connectionWaitTimeout = Duration.ofSeconds(timeoutInSeconds);
        return this;
    }

    /**
     * Get the JDBC connection timeout.
     *
     * @return JDBC connection timeout in seconds.
     */
    public int getJdbcConnectionTimeout() {
        return (int) this.connectionWaitTimeout.toSeconds();
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

    /**
     * Define a directory into which you want support information (like cluster logs) to be dumped.
     *
     * @param targetDirectory directory where to put the logs
     * @param exitType        type of exit for which the support information should be dumped
     * @return self
     */
    public ExasolContainer<T> withSupportInformationRecordedAtExit(final Path targetDirectory,
            final ExitType exitType) {
        this.supportInformationRetriever.monitorExit(exitType);
        this.supportInformationRetriever.mapTargetDirectory(targetDirectory);
        return this;
    }

    /**
     * Read and convert the self-signed TLS certificate used by the database in the container for database connections
     * and the RPC interface.
     *
     * @return the TLS certificate or an empty {@link Optional} when the certificate file does not exist.
     */
    public Optional<X509Certificate> getTlsCertificate() {
        return this.certificateProvider.getCertificate();
    }

    /**
     * Get the SHA256 fingerprint of the TLS certificate used by the database in the container for database connections
     * and the RPC interface.
     *
     * @return SHA256 fingerprint of the TLS certificate or an empty {@link Optional} when the certificate file does not
     *         exist.
     */
    public Optional<String> getTlsCertificateFingerprint() {
        return this.certificateProvider.getSha256Fingerprint();
    }

    // [impl->dsn~access-via-ssh~1]
    private void copyAuthorizedKeys(final DockerAccess accessProvider) {
        withCopyToContainer(accessProvider.getSshKeys().getPublicKeyTransferable(), "/root/.ssh/authorized_keys");
    }

    // [impl->dsn~detect-if-docker-exec-is-possible~1]
    DockerAccess createDockerAccess() {
        final Path dir = getTemporaryCredentialsDirectory();
        ensureExists(dir);
        return DockerAccess.builder() //
                .temporaryCredentialsDirectory(dir) //
                .sshKeys(getSshKeys()) //
                .dockerProbe(this::probeFile) //
                .sessionBuilderProvider(this::getSessionBuilder) //
                .build();
    }

    private static void ensureExists(final Path directory) {
        if (Files.isDirectory(directory)) {
            return;
        }
        try {
            Files.createDirectories(directory);
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    // [impl->dsn~access-via-ssh~1]
    private SshKeys getSshKeys() {
        try {
            return SshKeys.builder() //
                    .privateKey(getTemporaryCredentialsDirectory().resolve("id_rsa")) //
                    .publicKey(getTemporaryCredentialsDirectory().resolve("id_rsa.pub")) //
                    .build();
        } catch (final JSchException | IOException exception) {
            throw new ExasolContainerInitializationException("Could not create SSH key pair", exception);
        }
    }

    /**
     * Get the path to the directory where the Exasol container stores temporary credentials.
     *
     * @return directory for temporary credentials
     */
    Path getTemporaryCredentialsDirectory() {
        return this.temporaryCredentialsDirectory;
    }

    SessionBuilder getSessionBuilder() {
        return new SessionBuilder() //
                .user(SSH_USER) //
                .host(getHost()) //
                .port(getMappedPort(SSH_PORT)) //
                .config("StrictHostKeyChecking", "no");
    }

    /**
     * Check if a specific file inside the docker container does exist
     *
     * @param path path of the file to check for existence
     * @return {@code true} if the file exists.
     */
    // [impl->dsn~detect-if-docker-exec-is-possible~1]
    public ExecResult probeFile(final String path) {
        try {
            final ExecResult r = super.execInContainer(StandardCharsets.UTF_8, "test", "-f", path);
            if (r.getExitCode() != 0) {
                LOGGER.debug("File not found: {}, exit code: {}", path, r.getExitCode());
            }
            return r;
        } catch (UnsupportedOperationException | IOException exception) {
            throw new SshException(ExaError.messageBuilder("E-ETC-22") //
                    .message("Failed to probe existence of file {{path}}", path) //
                    .toString(), exception);
        } catch (final InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new SshException("Probing file " + path + "was interrupted", interruptedException);
        }
    }
}
