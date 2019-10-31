package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// This test contains test cases that modify the configuration of the container. Don't add test
// cases that depend on the default settings!
@Testcontainers
class ExasolContainerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainerTest.class);

    @Container
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>(
            ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE);

    @BeforeAll
    static void beforeAll() {
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

    @Test
    public void testWithUsername() {
        final String expectedUsername = "Johnathan Smith";
        assertThat(container.withUsername(expectedUsername).getUsername(), equalTo(expectedUsername));
    }

    @Test
    public void testWithPassword() {
        final String expectedPwd = "open sesame!";
        assertThat(container.withPassword(expectedPwd).getPassword(), equalTo(expectedPwd));
    }
}