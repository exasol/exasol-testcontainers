package com.exasol.containers.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.ExasolContainerConstants;
import com.jcraft.jsch.Session;

/**
 * <ul>
 * <li>Store SSH public/private key pair
 * <li>Detect mode for connecting to the docker container and tell if the container supports Docker Exec.
 * <li>enable to create and instance of {@link Ssh} in order to connect to docker container via SSH.
 * </ul>
 */
public class DockerAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerAccess.class);

    enum Mode {
        UNKNOWN, SSH, DOCKER_EXEC;
    }

    /**
     * @return a builder for a new instance of {@link DockerAccess}.
     */
    public static Builder builder() {
        return new Builder();
    }

    private Mode mode = Mode.UNKNOWN;
    private SessionBuilderProvider sessionBuilderProvider;
    private DockerProber dockerProber;
    private SshKeys sshKeys;
    private Ssh ssh = null;

    private DockerAccess() {
        // only instantiated by {@link Builder}
    }

    /**
     * @return SSH public/private key pair
     */
    public SshKeys getSshKeys() {
        return this.sshKeys;
    }

    /**
     * @return true if docker container supports docker exec
     */
    public boolean supportsDockerExec() {
        return getMode() == Mode.DOCKER_EXEC;
    }

    /**
     * @return either {@link Mode#DOCKER_EXEC} or {@link Mode#SSH}.
     */
    Mode getMode() {
        if (this.mode == Mode.UNKNOWN) {
            final ExecResult result = this.dockerProber.probeFile(ExasolContainerConstants.CLUSTER_CONFIGURATION_PATH);
            if (result.getExitCode() == 0) {
                this.mode = Mode.DOCKER_EXEC;
            } else {
                LOGGER.info("Docker container requires SSH access");
                this.mode = Mode.SSH;
            }
        }
        return this.mode;
    }

    /**
     * @return instance of {@link Ssh} providing SSH functionality
     */
    public Ssh getSsh() {
        if (this.ssh == null) {
            final Session session = this.sessionBuilderProvider.get() //
                    .identity(this.sshKeys.getIdentityProvider()) //
                    .build();
            this.ssh = new Ssh(session);
        }
        return this.ssh;
    }

    /**
     * Builds a session with parameters host, port, and user in order to create an SSH connection.
     */
    @FunctionalInterface
    public interface SessionBuilderProvider {
        SessionBuilder get() throws SshException;
    }

    /**
     * Used to probe a specific file inside docker container in order to find out which {@link Mode} may be used to
     * connect to docker container.
     */
    @FunctionalInterface
    public interface DockerProber {
        ExecResult probeFile(String path);
    }

    /**
     * Builder for a new instance of {@link DockerAccess}.
     */
    public static final class Builder {
        private final DockerAccess access = new DockerAccess();

        private Builder() {
            // use static method builder()
        }

        /**
         * @param sessionBuilderProvider provide an instance of {@link SessionBuilder} to build a session to instantiate
         *                               {@link Ssh} in method {@link DockerAccess#getSsh()}.
         * @return this for fluent programming
         */
        public Builder sessionBuilderProvider(final SessionBuilderProvider sessionBuilderProvider) {
            this.access.sessionBuilderProvider = sessionBuilderProvider;
            return this;
        }

        /**
         * @param dockerProber {@link DockerProber} used to check for existence of file
         *                     {@link ExasolContainerConstants#CLUSTER_CONFIGURATION_PATH}. If this file exists then the
         *                     container is rated to support Docker Exec.
         * @return this for fluent programming
         */
        public Builder dockerProber(final DockerProber dockerProber) {
            this.access.dockerProber = dockerProber;
            return this;
        }

        /**
         * @param sshKeys pair of public and private SSH key to use for SSH connection
         * @return this for fluent programming
         */
        public Builder sshKeys(final SshKeys sshKeys) {
            this.access.sshKeys = sshKeys;
            return this;
        }

        /**
         * @return new instance of {@link DockerAccess}
         */
        public DockerAccess build() {
            return this.access;
        }
    }
}
