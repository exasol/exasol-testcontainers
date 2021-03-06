package com.exasol.containers.status;

import static com.exasol.containers.ExasolService.BUCKETFS;
import static com.exasol.containers.status.ServiceStatus.NOT_CHECKED;
import static com.exasol.containers.status.ServiceStatus.READY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("fast")
class ContainerStatusTest {
    @Test
    void testGetContainerId() throws Exception {
        final String containerId = "the_id";
        final ContainerStatus status = ContainerStatus.create(containerId);
        assertThat(status.getContainerId(), equalTo(containerId));
    }

    @Test
    void testServiceStatusNotCheckedByDefault() throws Exception {
        assertThat(ContainerStatus.create("irrelevant").getServiceStatus(BUCKETFS), equalTo(NOT_CHECKED));
    }

    @Test
    void testSetServiceStatus() throws Exception {
        final ContainerStatus status = ContainerStatus.create("irrelevant");
        status.setServiceStatus(BUCKETFS, READY);
        assertThat(status.getServiceStatus(BUCKETFS), equalTo(READY));
    }

    @CsvSource({ "NOT_CHECKED, false", "NOT_READY, false", "READY, true" })
    @ParameterizedTest
    void testIsServiceReady(final ServiceStatus serviceStatus, final boolean ready) {
        final ContainerStatus status = ContainerStatus.create("irrelevant");
        status.setServiceStatus(BUCKETFS, serviceStatus);
        assertThat(status.isServiceReady(BUCKETFS), equalTo(ready));
    }

    @Test
    void testAddAllAppliedWorkarounds() {
        final ContainerStatus status = ContainerStatus.create("irrelevant");
        status.addAllAppliedWorkarounds(Set.of("A", "B"));
        assertThat(status.getAppliedWorkarounds(), containsInAnyOrder("A", "B"));
    }
}