package com.exasol.exaoperation.plugin;

import static com.exasol.exaoperation.plugin.PluginStub.PLUGIN_PACKAGE_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;
import com.exasol.exaoperation.ExaOperationEmulatorException;

@ExtendWith(MockitoExtension.class)
@Testcontainers
class PluginIT {
    private final static Logger LOGGER = LoggerFactory.getLogger(PluginIT.class);
    @Container
    private final static ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withLogConsumer(new Slf4jLogConsumer(LOGGER)).withRequiredServices();
    private static Plugin plugin;
    @Mock
    private org.testcontainers.containers.Container<? extends org.testcontainers.containers.Container<?>> containerMock;

    @BeforeAll
    static void beforeAll() {
        plugin = CONTAINER.getExaOperation().installPluginPackage(PLUGIN_PACKAGE_PATH);
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
        ExecResult execResult = assertDoesNotThrow(() -> plugin.status());
        assertAll(
                "Status Result"
                , () -> assertThat(execResult.getExitCode(), equalTo(0))
                , () -> assertThat(execResult.getStdout(), equalTo("status script called\n"))
        );
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
        when(this.containerMock.execInContainer(any())).thenThrow(exception);
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
        List<String> plugins = CONTAINER.getExaOperation().getPluginNames();
        // There should only be one plugin
        assertThat(plugins, equalTo(Collections.singletonList(plugin.getName())));
    }

    @Test
    void testListFunctions() {
        List<String> pluginFunctions = plugin.listFunctions();
        assertAll(
                "Function List"
                , () -> assertThat(pluginFunctions.size(), equalTo(7))
                , () -> assertThat(pluginFunctions, hasItem(containsString("Start plugin service")))
        );
    }

    @Test
    void testInvalidFunctionCall() {
        assertThrows(ExaOperationEmulatorException.class, () -> plugin.callFunction("START", null));
    }
}
