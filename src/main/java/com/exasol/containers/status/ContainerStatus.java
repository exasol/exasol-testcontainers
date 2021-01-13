package com.exasol.containers.status;

import java.io.Serializable;
import java.util.*;

import javax.annotation.processing.Generated;

import com.exasol.containers.ExasolService;

/**
 * State information about the Exasol docker container.
 */
public final class ContainerStatus implements Serializable {
    private static final long serialVersionUID = 5014025686797381100L;
    private final String containerId;
    private final Map<ExasolService, ServiceStatus> serviceStatuses = new EnumMap<>(ExasolService.class);

    private ContainerStatus(final String containerId) {
        this.containerId = containerId;
        for (final ExasolService service : ExasolService.values()) {
            this.serviceStatuses.put(service, ServiceStatus.NOT_CHECKED);
        }
    }

    /**
     * Create container state for the container with the given ID.
     *
     * @param containerId docker container ID
     * @return container state
     */
    public static ContainerStatus create(final String containerId) {
        return new ContainerStatus(containerId);
    }

    /**
     * Get the ID of the container this status belongs to.
     *
     * @return container ID
     */
    public String getContainerId() {
        return this.containerId;
    }

    /**
     * Get the status of the given service.
     *
     * @param service service for which the status should be provided
     * @return status of the service
     */
    public ServiceStatus getServiceStatus(final ExasolService service) {
        return this.serviceStatuses.get(service);
    }

    /**
     * Check if a service is ready.
     *
     * @param service service to be checked
     * @return {@code true} if the service is ready
     */
    public boolean isServiceReady(final ExasolService service) {
        return this.serviceStatuses.get(service) == ServiceStatus.READY;
    }

    /**
     * Set the status for a given service
     *
     * @param service       service for which the status should be changed
     * @param serviceStatus new status
     */
    public void setServiceStatus(final ExasolService service, final ServiceStatus serviceStatus) {
        this.serviceStatuses.put(service, serviceStatus);
    }

    @Generated("org.eclipse.Eclipse")
    @Override
    public int hashCode() {
        return Objects.hash(this.containerId, this.serviceStatuses);
    }

    @Generated("org.eclipse.Eclipse")
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ContainerStatus)) {
            return false;
        }
        final ContainerStatus other = (ContainerStatus) obj;
        return Objects.equals(this.containerId, other.containerId)
                && Objects.equals(this.serviceStatuses, other.serviceStatuses);
    }
}