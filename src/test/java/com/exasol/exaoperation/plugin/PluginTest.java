package com.exasol.exaoperation.plugin;

import static com.exasol.exaoperation.plugin.PluginStub.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PluginTest {

    private Plugin pluginPackage;

    @BeforeEach
    void beforeEach() {
        this.pluginPackage = new Plugin(PLUGIN_PACKAGE_PATH, null);
    }

    @Test
    void testGetNameFromPackage() {
        assertThat(this.pluginPackage.getName(), equalTo(PLUGIN_NAME));
    }

    @ValueSource(strings = { "Plugin.Foo+Bar-1.0.0.pkg", "Foo.Bar-1.0.0.pkg", "Plugin-Foo.Bar-1.0.0.pkg" })
    @ParameterizedTest
    void testGetNameFromPackageThrowsExceptionForIllegalNames(final String pathAsString) {
        assertThrows(IllegalArgumentException.class, () -> new Plugin(Path.of(pathAsString), null));
    }

    @Test
    void testGetSourcePath() {
        assertThat(this.pluginPackage.getSourcePath(), equalTo(PLUGIN_PACKAGE_PATH));
    }

    @Test
    void testGetFileName() {
        assertThat(this.pluginPackage.getFileName(), equalTo(PLUGIN_FILENAME));
    }
}