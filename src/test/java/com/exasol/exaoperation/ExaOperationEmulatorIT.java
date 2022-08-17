package com.exasol.exaoperation;

import static com.exasol.exaoperation.plugin.PluginStub.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolDockerImageReference;

@Tag("slow")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class ExaOperationEmulatorIT {

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>()
            .withRequiredServices();

    @BeforeAll
    static void beforeAll() {
        assumeExaOperations();
    }

    private static void assumeExaOperations() {
        final ExasolDockerImageReference dockerImageReference = EXASOL.getDockerImageReference();
        assumeTrue(!dockerImageReference.hasMajor() || (dockerImageReference.getMajor() < 8));
    }

    @Order(1)
    @Test
    void testHasPluginFalseBeforeInstallation() {
        assertThat(EXASOL.getExaOperation().hasPlugin(PLUGIN_NAME), equalTo(false));
    }

    // [itest->dsn~extracting-plug-in-packages~1]
    @Order(2)
    @Test
    void testInstallPluginPackage() throws UnsupportedOperationException, IOException, InterruptedException {
        final ExaOperation exaOperation = EXASOL.getExaOperation();
        exaOperation.installPluginPackage(PLUGIN_PACKAGE_PATH);
        final ExecResult result = EXASOL.execInContainer("ls", "/usr/opt/EXAplugins");
        assertThat(result.getStdout(), containsString(PLUGIN_NAME));
    }

    @Order(3)
    @Test
    void testHasPluginTrueAfterInstallation() {
        assertThat(EXASOL.getExaOperation().hasPlugin(PLUGIN_NAME), equalTo(true));
    }

    @Order(4)
    @Test
    void testGetPlugin() {
        assertThat(EXASOL.getExaOperation().getPlugin(PLUGIN_NAME).getName(), equalTo(PLUGIN_NAME));
    }

    @Order(5)
    @Test
    void testSecondInstallationThrows() {
        final ExaOperation exaOperation = EXASOL.getExaOperation();
        assertThrows(ExaOperationEmulatorException.class, () -> exaOperation.installPluginPackage(PLUGIN_PACKAGE_PATH));
    }

    @Test
    void testGetPluginThrowsIllegalArgumentExceptionForUnknownPluginName() {
        final ExaOperation exaOperation = EXASOL.getExaOperation();
        assertThrows(IllegalArgumentException.class, () -> exaOperation.getPlugin("Non.Existant-1.0.0"));
    }

    @Test
    void testInstallPluginPackageThrowsExceptionOnNonExistantPath() {
        final ExaOperation exaOperation = EXASOL.getExaOperation();
        final Path nonexistentPath = Path.of("does", "not", "exist");
        assertThrows(IllegalArgumentException.class, () -> exaOperation.installPluginPackage(nonexistentPath));
    }

    @Test
    void testBrokenPackageException() {
        final ExaOperation exaOperation = EXASOL.getExaOperation();
        final ExaOperationEmulatorException exception = assertThrows(ExaOperationEmulatorException.class,
                () -> exaOperation.installPluginPackage(BROKEN_PACKAGE_PATH));
        assertAll("exception", () -> assertThat(exception.getMessage(), equalTo("Unable to install plug-in.")), () -> {
            assertNotNull(exception.getCause());
            assertThat(exception.getCause().getMessage(), startsWith("Extract plugin package"));
        });
    }
}