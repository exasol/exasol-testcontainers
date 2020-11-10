package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;

public class ExasolContainerResuseIT {
    public static final String TESTCONTAINERS_REUSE_ENABLE = "testcontainers.reuse.enable";
    static String propertyBackup;

    @BeforeAll
    static void beforeAll() throws NoSuchFieldException, IllegalAccessException {
        propertyBackup = (String) getTestcontainerProperties().getOrDefault(TESTCONTAINERS_REUSE_ENABLE, "false");
    }

    @AfterAll
    static void afterAll() throws NoSuchFieldException, IllegalAccessException {
        getTestcontainerProperties().setProperty(TESTCONTAINERS_REUSE_ENABLE, propertyBackup);
    }

    private static Properties getTestcontainerProperties() throws NoSuchFieldException, IllegalAccessException {
        final TestcontainersConfiguration testcontainersConfiguration = TestcontainersConfiguration.getInstance();
        final Field environmentPropertiesField = testcontainersConfiguration.getClass()
                .getDeclaredField("userProperties");
        environmentPropertiesField.setAccessible(true);
        return (Properties) environmentPropertiesField.get(testcontainersConfiguration);
    }

    // [itest->dsn~keep-container-running-if-reuse~1]
    @Test
    void testContainerIsNotStoppedIfReuseIsEnabled() throws NoSuchFieldException, IllegalAccessException {
        getTestcontainerProperties().setProperty(TESTCONTAINERS_REUSE_ENABLE, "true");
        assertThat(startAndStopContainerAndCheckIfRunning(), equalTo(true));
    }

    // [itest->dsn~keep-container-running-if-reuse~1]
    @Test
    void testContainerIsStoppedIfReuseIsDisabledViaEnvVar() throws NoSuchFieldException, IllegalAccessException {
        getTestcontainerProperties().setProperty(TESTCONTAINERS_REUSE_ENABLE, "false");
        assertThat(startAndStopContainerAndCheckIfRunning(), equalTo(false));
    }

    // [itest->dsn~purging~1]
    @Test
    void testDatabaseIsPurgedBeforeReuse() throws NoSuchFieldException, IllegalAccessException, SQLException {
        getTestcontainerProperties().setProperty(TESTCONTAINERS_REUSE_ENABLE, "true");
        final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>().withReuse(true);
        container.start();
        final Connection connection = container.createConnectionForUser(container.getUsername(),
                container.getPassword());
        final Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE SCHEMA TEST;");
        container.stop();
        final ExasolContainer<? extends ExasolContainer<?>> container2 = new ExasolContainer<>().withReuse(true);
        container2.start();
        assertDoesNotThrow(() -> statement.executeUpdate("CREATE SCHEMA TEST;"));
        container2.stop();
    }

    private boolean startAndStopContainerAndCheckIfRunning() {
        final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>().withReuse(true);
        container.start();
        final DockerClient dockerClient = container.getDockerClient();
        final String containerId = container.getContainerId();
        container.stop();
        return isContainerRunning(dockerClient, containerId);
    }

    private boolean isContainerRunning(final DockerClient dockerClient, final String containerId) {
        final List<Container> running = dockerClient.listContainersCmd().withStatusFilter(List.of("running")).exec();
        return running.stream().anyMatch(eachContainer -> eachContainer.getId().equals(containerId));
    }
}
