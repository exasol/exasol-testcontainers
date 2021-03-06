package com.exasol.exaoperation;

import static com.exasol.exaoperation.plugin.PluginStub.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Tag("slow")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class ExaOperationEmulatorIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withRequiredServices();

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

    @Order(5)
    @Test
    void testSecondInstallationThrows() {
        assertThrows(ExaOperationEmulatorException.class,
                () -> CONTAINER.getExaOperation().installPluginPackage(PLUGIN_PACKAGE_PATH));
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

    @Test
    void testBrokenPackageException() {
        final ExaOperationEmulatorException exception = assertThrows(ExaOperationEmulatorException.class,
                () -> CONTAINER.getExaOperation().installPluginPackage(BROKEN_PACKAGE_PATH));
        assertAll("exception", () -> assertThat(exception.getMessage(), equalTo("Unable to install plug-in.")), () -> {
            assertNotNull(exception.getCause());
            assertThat(exception.getCause().getMessage(), startsWith("Extract plugin package"));
        });
    }
}