package com.exasol.exaoperation.plugin;

import java.nio.file.Path;

/**
 * A plugin stub
 */
public final class PluginStub {
    /** Version of the plugin. */
    public static final String PLUGIN_VERSION = "1.0.0";
    /** Build date of the plugin. */
    public static final String PLUGIN_BUILD_DATE = "2020-02-02";
    /** Plugin name. */
    public static final String PLUGIN_NAME = "Testing.Stub-" + PLUGIN_VERSION;
    /** Filename of the plugin. */
    public static final String PLUGIN_FILENAME = "Plugin." + PLUGIN_NAME + "-" + PLUGIN_BUILD_DATE + ".pkg";
    /** Broken plugin filename. */
    public static final String BROKEN_PLUGIN_FILENAME = "Plugin.Testing.Broken-1.0.0-2020-03-20.pkg";
    /** Plugin base path. */
    public static final Path PLUGIN_BASEPATH = Path.of("src", "test", "sh");
    /** Plugin package path. */
    public static final Path PLUGIN_PACKAGE_PATH = PLUGIN_BASEPATH.resolve(PLUGIN_FILENAME);
    /** Broken plugin filename. */
    public static final Path BROKEN_PACKAGE_PATH = PLUGIN_BASEPATH.resolve(BROKEN_PLUGIN_FILENAME);

    private PluginStub() {
        // prevent instantiation
    }
}