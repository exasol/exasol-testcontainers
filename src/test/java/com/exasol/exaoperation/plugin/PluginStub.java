package com.exasol.exaoperation.plugin;

import java.nio.file.Path;

public final class PluginStub {
    public static final String PLUGIN_VERSION = "1.0.0";
    public static final String PLUGIN_BUILD_DATE = "2020-02-02";
    public static final String PLUGIN_NAME = "Testing.Stub-" + PLUGIN_VERSION;
    public static final String PLUGIN_FILENAME = "Plugin." + PLUGIN_NAME + "-" + PLUGIN_BUILD_DATE + ".pkg";
    public static final Path PLUGIN_PACKAGE_PATH = Path.of("src", "test", "sh", PLUGIN_FILENAME);

    private PluginStub() {
        // prevent instantiation
    }
}