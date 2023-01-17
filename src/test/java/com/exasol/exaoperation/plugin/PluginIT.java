package com.exasol.exaoperation.plugin;

import static com.exasol.exaoperation.plugin.PluginStub.PLUGIN_PACKAGE_PATH;
import static com.exasol.testutil.VarArgsMatcher.anyStrings;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolDockerImageReference;
import com.exasol.exaoperation.ExaOperationEmulatorException;

@Tag("slow")
@ExtendWith(MockitoExtension.class)
@Testcontainers
class PluginIT {

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()
            .withRequiredServices();
    private static Plugin plugin;

    @Mock
    private org.testcontainers.containers.Container<? extends org.testcontainers.containers.Container<?>> containerMock;

    @BeforeAll
    static void beforeAll() {
        assumeExaOperations();
        plugin = container.getExaOperation().installPluginPackage(PLUGIN_PACKAGE_PATH);
    }

    private static void assumeExaOperations() {
        final ExasolDockerImageReference dockerImageReference = container.getDockerImageReference();
        assumeTrue(!dockerImageReference.hasMajor() || (dockerImageReference.getMajor() < 8));
    }

    @AfterAll
    static void tearDown() {
        container.close();
    }

    // [itest->dsn~installing-plug-ins~1]
    @Test
    void testInstall() {
        assertThat(plugin.install().getStdout(), equalTo("install script called\n"));
    }

    // [itest->dsn~starting-plug-ins~1]
    @Test
    void testStart() {
        assertThat(plugin.start().getStdout(), equalTo("start script called\n"));
    }

    // [itest->dsn~stopping-plug-ins~1]
    @Test
    void testStop() {
        assertThat(plugin.stop().getStdout(), equalTo("stop script called\n"));
    }

    // [itest->dsn~restarting-plug-ins~1]
    @Test
    void testRestart() {
        assertThat(plugin.restart().getStdout(), equalTo("restart script called\n"));
    }

    // [itest->dsn~getting-the-plug-ins-status~1]
    @Test
    void testStatus() {
        final ExecResult execResult = assertDoesNotThrow(() -> plugin.status());
        assertAll("Status Result", () -> assertThat(execResult.getExitCode(), equalTo(0)),
                () -> assertThat(execResult.getStdout(), equalTo("status script called\n")));
    }

    // [itest->dsn~uninstalling-plug-ins~1]
    @Test
    void testUninstall() {
        assertThat(plugin.uninstall().getStdout(), equalTo("uninstall script called\n"));
    }

    @Test
    void testUnsupportedFunction() {
        assertThat(plugin.callFunction("function_does_not_exist").getExitCode(), equalTo(1));
    }

    @Test
    void testRunPluginScriptCatchesIoException()
            throws UnsupportedOperationException, IOException, InterruptedException {
        assertRunPlugInScriptCatchesException(new IOException());
    }

    private void assertRunPlugInScriptCatchesException(final Exception exception)
            throws IOException, InterruptedException {
        when(this.containerMock.execInContainer(anyStrings())).thenThrow(exception);
        final Plugin plugin = new Plugin(Path.of("Plugin.Irrelevant.Name-1.2.3"), this.containerMock);
        assertThrows(ExaOperationEmulatorException.class, plugin::install);
    }

    @Test
    void testRunPluginScriptCatchesUnsupportedOperationException()
            throws UnsupportedOperationException, IOException, InterruptedException {
        assertRunPlugInScriptCatchesException(new UnsupportedOperationException());
    }

    // [itest->dsn~listing-plug-ins~1]
    @Test
    void testListPlugins() {
        final List<String> plugins = container.getExaOperation().getPluginNames();
        // There should only be one plugin
        assertThat(plugins, equalTo(Collections.singletonList(plugin.getName())));
    }

    @Test
    void testListFunctions() {
        final List<String> pluginFunctions = plugin.listFunctions();
        assertAll("Function List", () -> assertThat(pluginFunctions.size(), equalTo(7)),
                () -> assertThat(pluginFunctions, hasItem(containsString("Start plugin service"))));
    }

    @Test
    void testInvalidFunctionCall() {
        assertThrows(ExaOperationEmulatorException.class, () -> plugin.callFunction("START", null));
    }
}
