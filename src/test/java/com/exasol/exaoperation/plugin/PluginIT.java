package com.exasol.exaoperation.plugin;

import static com.exasol.exaoperation.plugin.PluginStub.PLUGIN_PACKAGE_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Testcontainers
class PluginIT {
    private final static Logger LOGGER = LoggerFactory.getLogger(PluginIT.class);
    @Container
    private final static ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withLogConsumer(new Slf4jLogConsumer(LOGGER)).withRequiredServices();
    private static Plugin plugin;

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
        assertThat(plugin.status().getStdout(), equalTo("status script called\n"));
    }

    // [itest->dsn~uninstalling-plug-ins~1]
    @Test
    void testUnistall() {
        assertThat(plugin.uninstall().getStdout(), equalTo("uninstall script called\n"));
    }
}