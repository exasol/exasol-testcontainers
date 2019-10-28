package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class ExasolContainerTest {
    private static final int EXASOL_DATABASE_PORT = 8888;
    private static final String EXASOL_DOCKER_IMAGE_VERSION = "6.2.2-d1";
    private static final String EXASOL_DOCKER_IMAGE_ID = "exasol/docker-db";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainerTest.class);

    @Container
    private static ExasolContainer container = new ExasolContainer(
            EXASOL_DOCKER_IMAGE_ID + ":" + EXASOL_DOCKER_IMAGE_VERSION);

    @BeforeAll
    static void beforeAll() {
        container.waitingFor(Wait.forListeningPort());
        container.followOutput(new Slf4jLogConsumer(LOGGER));
    }

    @Test
    void testGetDriverClassName() {
        assertThat(container.getDriverClassName(), equalTo("com.exasol.jdbc.EXADriver"));
    }

    @Test
    public void testGetJdbcUrl() throws Exception {
        assertThat(container.getJdbcUrl(), matchesPattern("jdbc:exa:localhost:\\d{1,5}"));
    }

}