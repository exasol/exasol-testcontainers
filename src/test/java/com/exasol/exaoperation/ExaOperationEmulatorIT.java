package com.exasol.exaoperation;

import static com.exasol.exaoperation.plugin.PluginStub.PLUGIN_NAME;
import static com.exasol.exaoperation.plugin.PluginStub.PLUGIN_PACKAGE_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class ExaOperationEmulatorIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExaOperationEmulatorIT.class);
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withLogConsumer(new Slf4jLogConsumer(LOGGER)).withRequiredServices();

    @Order(1)
    @Test
    void testHasPluginFalseBeforeInstallation() {
        assertThat(CONTAINER.getExaOperation().hasPlugin(PLUGIN_NAME), equalTo(false));
    }

    // [itest->dsn~extracting-plug-in-packages~1]
    @Order(2)
    @Test
    void testInstallPluginPackage() throws UnsupportedOperationException, IOException, InterruptedException {
        final ExaOperation exaOperation = CONTAINER.getExaOperation();
        exaOperation.installPluginPackage(PLUGIN_PACKAGE_PATH);
        final ExecResult result = CONTAINER.execInContainer("ls", "/usr/opt/EXAplugins");
        assertThat(result.getStdout(), containsString(PLUGIN_NAME));
    }

    @Order(3)
    @Test
    void testHasPluginTrueAfterInstallation() {
        assertThat(CONTAINER.getExaOperation().hasPlugin(PLUGIN_NAME), equalTo(true));
    }

    @Order(4)
    @Test
    void testGetPlugin() {
        assertThat(CONTAINER.getExaOperation().getPlugin(PLUGIN_NAME).getName(), equalTo(PLUGIN_NAME));
    }

    @Test
    void testGetPluginThrowsIllegalArgumentExceptionForUnknownPluginName() {
        assertThrows(IllegalArgumentException.class, () -> CONTAINER.getExaOperation().getPlugin("Non.Existant-1.0.0"));
    }

    @Test
    void testInstallPluginPackageThrowsExceptionOnNonExistantPath() {
        assertThrows(IllegalArgumentException.class,
                () -> CONTAINER.getExaOperation().installPluginPackage(Path.of("does", "not", "exist")));
    }
}