package com.exasol.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Tag("slow")
@Testcontainers
class ClusterConfigAccessIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withRequiredServices();

    private ClusterConfiguration clusterConfiguration;

    @Test
    void testGetClusterConfiguration() {
        this.clusterConfiguration = CONTAINER.getClusterConfiguration();
        assertThat(this.clusterConfiguration.getBucketFsServiceConfiguration("bfsdefault").getName(),
                equalTo("bfsdefault"));
    }
}