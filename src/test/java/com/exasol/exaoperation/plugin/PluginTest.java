package com.exasol.exaoperation.plugin;

import static com.exasol.exaoperation.plugin.PluginStub.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("fast")
class PluginTest {
    private Plugin pluginPackage;

    @BeforeEach
    void beforeEach() {
        this.pluginPackage = new Plugin(PLUGIN_PACKAGE_PATH, null);
    }

    @Test
    void testGetName() {
        assertThat(this.pluginPackage.getName(), equalTo(PLUGIN_NAME));
    }

    @Test
    void testGetNameWithDash() {
        final Path path = Path.of("src", "test", "sh", "Plugin.Administration.Service.Exasol-DRS-0.7.pkg");
        final Plugin plugin = new Plugin(path, null);
        assertThat(plugin.getName(), equalTo("Administration.Service.Exasol-DRS-0.7"));
    }

    @ValueSource(strings = { "Plugin.Foo+Bar-1.0.0.pkg", "Foo.Bar-1.0.0.pkg", "Plugin-Foo.Bar-1.0.0.pkg", "" })
    @ParameterizedTest
    void testGetNameFromPackageThrowsExceptionForIllegalNames(final String pathAsString) {
        final Path path = Path.of(pathAsString);
        assertThrows(IllegalArgumentException.class, () -> new Plugin(path, null));
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