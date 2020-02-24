package com.exasol.exaoperation;

import java.nio.file.Path;

import com.exasol.exaoperation.plugin.Plugin;

/**
 * EXAoperation interface.
 */
public interface ExaOperation {
    /**
     * Install an EXAoperation plug-in.
     *
     * @param pluginPackagePath from where to install the plug-in package
     * @return control object for the installed plug-in
     *
     */
    public Plugin installPluginPackage(final Path pluginPackagePath);

    /**
     * Check if the plug-in with the give name is installed.
     *
     * @param pluginName name of the plug-in
     * @return {@code true} if the plug-in is installed
     */
    public boolean hasPlugin(String pluginName);
}