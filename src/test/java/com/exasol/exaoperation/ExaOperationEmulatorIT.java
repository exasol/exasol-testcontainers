package com.exasol.exaoperation;

import static com.exasol.exaoperation.plugin.PluginStub.PLUGIN_NAME;
import static com.exasol.exaoperation.plugin.PluginStub.PLUGIN_PACKAGE_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Testcontainers
class ExaOperationEmulatorIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExaOperationEmulatorIT.class);
    @Container
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()
            .withLogConsumer(new Slf4jLogConsumer(LOGGER)).withRequiredServices();

    @Order(1)
    @Test
    void testHasPluginFalseBeforeInstallation() {
        assertThat(container.getExaOperation().hasPlugin(PLUGIN_NAME), equalTo(false));
    }

    // [itest->dsn~extracting-plug-in-packages~1]
    @Order(2)
    @Test
    void testInstall() throws UnsupportedOperationException, IOException, InterruptedException {
        final ExaOperation exaOperation = container.getExaOperation();
        exaOperation.installPluginPackage(PLUGIN_PACKAGE_PATH);
        final ExecResult result = container.execInContainer("ls", "/usr/opt/EXAplugins");
        assertThat(result.getStdout(), containsString(PLUGIN_NAME));
    }

    @Order(3)
    @Test
    void testHasPluginTrueAfterInstallation() {
        assertThat(container.getExaOperation().hasPlugin(PLUGIN_NAME), equalTo(true));
    }
}