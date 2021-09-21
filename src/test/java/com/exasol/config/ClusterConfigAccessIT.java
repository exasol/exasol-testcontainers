package com.exasol.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
            .withRequiredServices().withReuse(true);

    private ClusterConfiguration clusterConfiguration;

    @Test
    void testGetClusterConfiguration() {
        this.clusterConfiguration = CONTAINER.getClusterConfiguration();
        assertThat(this.clusterConfiguration.getBucketFsServiceConfiguration("bfsdefault").getName(),
                equalTo("bfsdefault"));
    }

    @Test
    void testGetAuthenticationToken() {
        this.clusterConfiguration = CONTAINER.getClusterConfiguration();
        final String token = this.clusterConfiguration.getAuthenticationToken();
        assertThat(token, not(emptyOrNullString()));
        assertThat(token.length(), greaterThan(20));
    }

    @Test
    void testGetTlsCertificatePath() {
        this.clusterConfiguration = CONTAINER.getClusterConfiguration();
        final String token = this.clusterConfiguration.getTlsCertificatePath();
        assertThat(token, equalTo("/exa/etc/ssl/ssl.crt"));
    }
}