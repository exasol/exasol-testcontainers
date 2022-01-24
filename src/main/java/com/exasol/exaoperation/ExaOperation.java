package com.exasol.exaoperation;

import java.nio.file.Path;
import java.util.List;

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
     */
    public Plugin installPluginPackage(final Path pluginPackagePath);

    /**
     * Check if the plug-in with the give name is installed.
     *
     * @param pluginName name of the plug-in
     * @return {@code true} if the plug-in is installed
     */
    public boolean hasPlugin(final String pluginName);

    /**
     * Get the plug-in control object for a plug-in name.
     *
     * @param pluginName name of the plug-in
     * @return plug-in control object
     */
    public Plugin getPlugin(final String pluginName);

    /**
     * Get the list of uploaded plugins.
     * 
     * @return list of plugin names suitable for {@link #hasPlugin(String)} and {@link #getPlugin(String)}
     */
    public List<String> getPluginNames();
}
