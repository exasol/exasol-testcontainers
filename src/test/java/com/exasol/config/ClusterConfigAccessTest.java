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
import com.exasol.containers.ExasolContainerConstants;

@Testcontainers
class ClusterConfigAccessTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterConfigAccessTest.class);

    @Container
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>(
            ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE).withLogConsumer(new Slf4jLogConsumer(LOGGER));

    private ClusterConfiguration clusterConfiguration;

    @Test
    void testGetClusterConfiguration() {
        this.clusterConfiguration = container.getClusterConfiguration();
        assertThat(this.clusterConfiguration.getBucketFsServiceConfiguration("bfsdefault").getName(),
                equalTo("bfsdefault"));
    }
}