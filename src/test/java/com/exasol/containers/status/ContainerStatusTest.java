package com.exasol.containers.status;

import static com.exasol.containers.ExasolService.BUCKETFS;
import static com.exasol.containers.status.ServiceStatus.NOT_CHECKED;
import static com.exasol.containers.status.ServiceStatus.READY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContainerStatusTest {
    @Test
    void testGetContainerId() throws Exception {
        final String containerId = "the_id";
        final ContainerStatus state = ContainerStatus.create(containerId);
        assertThat(state.getContainerId(), equalTo(containerId));
    }

    @Test
    void testServiceStatusNotCheckedByDefault() throws Exception {
        assertThat(ContainerStatus.create("irrelevant").getServiceStatus(BUCKETFS), equalTo(NOT_CHECKED));
    }

    @Test
    void testSetServiceStatus() throws Exception {
        final ContainerStatus state = ContainerStatus.create("irrelevant");
        state.setServiceStatus(BUCKETFS, READY);
        assertThat(state.getServiceStatus(BUCKETFS), equalTo(READY));
    }

    @CsvSource({ "NOT_CHECKED, false", "NOT_READY, false", "READY, true" })
    @ParameterizedTest
    void testIsServiceReady(final ServiceStatus serviceStatus, final boolean ready) {
        final ContainerStatus state = ContainerStatus.create("irrelevant");
        state.setServiceStatus(BUCKETFS, serviceStatus);
        assertThat(state.isServiceReady(BUCKETFS), equalTo(ready));
    }
}