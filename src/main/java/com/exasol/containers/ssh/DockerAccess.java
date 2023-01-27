package com.exasol.containers.ssh;

import java.io.File;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.ExasolContainerConstants;
import com.exasol.errorreporting.ExaError;
import com.jcraft.jsch.Session;

/**
 * <ul>
 * <li>Store SSH public/private key pair
 * <li>Detect mode for connecting to the docker container and tell if the container supports Docker Exec.
 * <li>enable to create and instance of {@link Ssh} in order to connect to docker container via SSH.
 * </ul>
 */
public final class DockerAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerAccess.class);
    private final SessionBuilderProvider sessionBuilderProvider;
    private final DockerProbe dockerProbe;
    private final SshKeys sshKeys;
    private final Path temporaryCredentialsDirectory;
    // The access mode is determined lazily and cached for later use.
    private Mode cachedAccessMode = Mode.UNKNOWN;
    // The SSH connection is created lazily and cached for later use.
    private Ssh cachedSshConnection = null;

    enum Mode {
        UNKNOWN, SSH, DOCKER_EXEC
    }

    private DockerAccess(final Builder builder) {
        this.sessionBuilderProvider = builder.sessionBuilderProvider;
        this.dockerProbe = builder.dockerProbe;
        this.sshKeys = builder.sshKeys;
        this.temporaryCredentialsDirectory = builder.temporaryCredentialsDirectory;
    }

    /**
     * Get the SSH key pair.
     * 
     * @return SSH public / private key pair
     */
    public SshKeys getSshKeys() {
        return this.sshKeys;
    }

    /**
     * Check whether we can use {@code docker exec} to access the docker container.
     * 
     * @return {@code true} if docker container supports docker exec
     */
    // [impl->dsn~detect-if-docker-exec-is-possible~1]
    public boolean supportsDockerExec() {
        return getMode() == Mode.DOCKER_EXEC;
    }

    /**
     * Get the access variant.
     * <p>
     * Note that the mode is determined upon the first call of this function and then cached to improve the performance
     * of later uses.
     * </p>
     * 
     * @return either {@link Mode#DOCKER_EXEC} or {@link Mode#SSH}.
     */
    synchronized Mode getMode() {
        if (this.cachedAccessMode == Mode.UNKNOWN) {
            final ExecResult result = this.dockerProbe.probeFile(ExasolContainerConstants.CLUSTER_CONFIGURATION_PATH);
            if (result.getExitCode() == 0) {
                LOGGER.trace("Docker container supports docker exec");
                this.cachedAccessMode = Mode.DOCKER_EXEC;
            } else {
                LOGGER.trace("Docker container requires SSH access");
                this.cachedAccessMode = Mode.SSH;
            }
        }
        return this.cachedAccessMode;
    }

    /**
     * Get an SSH connection.
     * <p>
     * Note that the SSH connection is created upon the first call of this function and then cached to improve the
     * performance of later uses.
     * </p>
     *
     * @return instance of {@link Ssh} providing SSH functionality
     */
    // [impl->dsn~access-via-ssh~1]
    public synchronized Ssh getSsh() {
        if (this.cachedSshConnection == null) {
            createTemporaryCredentialDirectoryIfMissing();
            final Session session = this.sessionBuilderProvider.get() //
                    .identity(this.sshKeys.getIdentityProvider()) //
                    .build();
            this.cachedSshConnection = new Ssh(session);
        }
        return this.cachedSshConnection;
    }

    // [impl->dsn~auto-create-directory-for-temporary-credentials~1]
    private void createTemporaryCredentialDirectoryIfMissing() {
        final File directory = this.temporaryCredentialsDirectory.toFile();
        if (!directory.exists() && !directory.mkdirs()) {
            ExaError.messageBuilder("F-ETC-23") //
                    .message("Unable to create directory for temporary credentials: {{path}}") //
                    .parameter("path", directory.toString());
        }
    }

    /**
     * Builds a session with parameters host, port, and user in order to create an SSH connection.
     */
    @FunctionalInterface
    public interface SessionBuilderProvider {
        /**
         * Create a new instance of {@link SessionBuilder} to be used by {@link DockerAccess} to build a new session and
         * connect to the Docker container.
         *
         * @return instance of {@link SessionBuilder}
         * @throws SshException in case SessionBuilder creation fails
         */
        SessionBuilder get() throws SshException;
    }

    /**
     * Used to probe a specific file inside docker container in order to find out which {@link Mode} may be used to
     * connect to docker container.
     */
    @FunctionalInterface
    public interface DockerProbe {
        /**
         * @param path path to be checked for existence
         * @return result of probing
         */
        ExecResult probeFile(String path);
    }

    /**
     * @return a builder for a new instance of {@link DockerAccess}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for a new instance of {@link DockerAccess}.
     */
    public static final class Builder {
        private SessionBuilderProvider sessionBuilderProvider;
        private DockerProbe dockerProbe;
        private SshKeys sshKeys;
        private Path temporaryCredentialsDirectory;

        private Builder() {
            // use static method builder()
        }

        /**
         * Set the {@link SessionBuilderProvider}.
         * 
         * @param sessionBuilderProvider provide an instance of {@link SessionBuilder} to build a session to instantiate
         *                               {@link Ssh} in method {@link DockerAccess#getSsh()}.
         * @return this for fluent programming
         */
        public Builder sessionBuilderProvider(final SessionBuilderProvider sessionBuilderProvider) {
            this.sessionBuilderProvider = sessionBuilderProvider;
            return this;
        }

        /**
         * Set the docker probe.
         * <p>
         * Checks the existence of the cluster configuration file to determine if {@code docker exec} can be used to
         * access the docker container.
         * </p>
         * 
         * @param dockerProbe {@link DockerProbe} used to check for existence of file
         *                    {@link ExasolContainerConstants#CLUSTER_CONFIGURATION_PATH}.
         * @return this for fluent programming
         */
        public Builder dockerProbe(final DockerProbe dockerProbe) {
            this.dockerProbe = dockerProbe;
            return this;
        }

        /**
         * Set the temporary SSH keys.
         * 
         * @param sshKeys pair of public and private SSH key to use for SSH connection
         * @return this for fluent programming
         */
        public Builder sshKeys(final SshKeys sshKeys) {
            this.sshKeys = sshKeys;
            return this;
        }

        /**
         * Create a new instance of the docker access.
         *
         * @return new instance of {@link DockerAccess}
         */
        public DockerAccess build() {
            return new DockerAccess(this);
        }

        /**
         * Set the directory in which temporary credentials are stored.
         * 
         * @param temporaryCredentialsDirectory directory in which to store temporary credentials
         * @return this for fluent programming
         */
        public Builder temporaryCredentialsDirectory(final Path temporaryCredentialsDirectory) {
            this.temporaryCredentialsDirectory = temporaryCredentialsDirectory;
            return this;
        }
    }
}
