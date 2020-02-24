package com.exasol.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Testcontainers
class ClusterConfigAccessIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterConfigAccessIT.class);
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withLogConsumer(new Slf4jLogConsumer(LOGGER)).withRequiredServices();

    private ClusterConfiguration clusterConfiguration;

    @Test
    void testGetClusterConfiguration() {
        this.clusterConfiguration = CONTAINER.getClusterConfiguration();
        assertThat(this.clusterConfiguration.getBucketFsServiceConfiguration("bfsdefault").getName(),
                equalTo("bfsdefault"));
    }
}