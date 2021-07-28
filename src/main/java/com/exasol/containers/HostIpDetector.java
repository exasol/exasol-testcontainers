package com.exasol.containers;

import java.util.Map;

import org.testcontainers.containers.Container;

import com.exasol.errorreporting.ExaError;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.NetworkSettings;

/**
 * Detector for the IP address of the host running the Exasol `docker-db`.
 */
public class HostIpDetector {
    private final Container<? extends Container<?>> container;

    /**
     * Create a new {@link HostIpDetector} instance.
     *
     * @param container container for which's host the IP should be detected
     */
    public HostIpDetector(final Container<? extends Container<?>> container) {
        this.container = container;
    }

    /**
     * Get the IP of a host on which a container runs.
     *
     * @return host IP
     */
    // [impl->dsn~host-ip-address-detection~1]
    public String getHostIp() {
        final NetworkSettings networkSettings = this.container.getContainerInfo().getNetworkSettings();
        final Map<String, ContainerNetwork> networks = networkSettings.getNetworks();
        if (networks.size() == 0) {
            throw new IllegalStateException(ExaError //
                    .messageBuilder("F-ETC-3") //
                    .message("Unable to determine host IP for {{docker-image}} container" + //
                            " because the docker network had no entries.") //
                    .parameter("docker-image", this.container.getDockerImageName(), "Docker image name") //
                    .toString());
        } else {
            return networks.values().iterator().next().getGateway();
        }
    }
}