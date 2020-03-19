package com.exasol.exaoperation.plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.exaoperation.ExaOperationEmulatorException;

/**
 * Representation of an EXAoperation plug-in.
 * <p>
 * These plug-ins are installed from a plug-in package, an archive file with a defined structure and contain scripts or
 * executables called in the various life cycle phases of the plug-in (e.g. for installation, start and stop).
 * </p>
 */
public class Plugin {
    public static final String PLUGIN_PACKAGE_PREFIX = "Plugin.";
    private static final Logger LOGGER = LoggerFactory.getLogger(Plugin.class);
    @SuppressWarnings("squid:S4784") // This is a test framework RegEx DoS attacks are unrealistic since this would mean
                                     // the testers are attacking themselves.
    private static final Pattern PLUGIN_PACKAGE_FILENAME_PATTERN = Pattern
            .compile("Plugin\\.([-.\\w]+?-\\d+(?:\\.\\d+)*).*?");
    private final Path sourcePath;
    private final String name;
    private final Container<? extends Container<?>> container;

    /**
     * Create a new instance of a {@link Plugin}.
     *
     * @param sourcePath path on the host where the plug-in originates.
     * @param container  parent container
     */
    public Plugin(final Path sourcePath, final Container<? extends Container<?>> container) {
        this.sourcePath = sourcePath;
        this.container = container;
        this.name = getPluginNameFromPackage(sourcePath);
    }

    private String getPluginNameFromPackage(final Path sourcePath) {
        final String filename = sourcePath.getFileName().toString();
        final Matcher matcher = PLUGIN_PACKAGE_FILENAME_PATTERN.matcher(filename);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Illegal plug-in package filename \"" + filename + "\". Must match: "
                    + PLUGIN_PACKAGE_FILENAME_PATTERN);
        }
    }

    /**
     * Get the name of the plug-in inside the package.
     *
     * @return plug-in name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the path on the host where the plug-in package is stored.
     *
     * @return source path on the host
     */
    public Path getSourcePath() {
        return this.sourcePath;
    }

    /**
     * Get the file name of the archive containing the plug-in.
     *
     * @return package file name
     */
    public String getFileName() {
        return this.sourcePath.getFileName().toString();
    }

    /**
     * Install the plug-in.
     *
     * @return result of executing the installation script.
     */
    // [impl->dsn~installing-plug-ins~1]
    public ExecResult install() {
        return callFunction("install");
    }

    /**
     * Call one of the plugin's functions.
     *
     * @param method name of the function to call. plugins should handle this case-insensitive
     * @return result of the function call
     */
    public ExecResult callFunction( final String method) {
        return callFunction( method, "" );
    }

    /**
     * Call one of the plugin's functions with the given argument.
     *
     * @param method name of the function to call. plugins should handle this case-insensitive
     * @param argument argument to the function call. Content and encoding depends on plugin and method.
     * @return result of the function call
     */
    public ExecResult callFunction( final String method, final String argument) {
        if( null == argument ) {
            throw new ExaOperationEmulatorException(
                    "Argument of Plugin::callFunction must never be null!" );
        }
        return callFunctionInternal( method, argument );
    }

    /**
     * Internal implementation of callFunction, distinguishing call with and without argument.
     *
     * @param method Name of function to call (1st argument)
     * @param argument optional second argument; null for none
     * @return result of function call
     */
    private ExecResult callFunctionInternal( final String method, final String argument ) {
        try {
            final String script = "/usr/opt/EXAplugins/" + this.name + "/exaoperation-gate/plugin-functions";
            LOGGER.info("Calling function \"{}\" of plug-in \"{}\".", method, this.name);
            if( null!=argument ) {
                return this.container.execInContainer( script, method, argument );
            } else {
                return this.container.execInContainer( script, method );
            }
        } catch (UnsupportedOperationException | IOException exception) {
            throw new ExaOperationEmulatorException(
                    "Unable to run \"" + method + ("\" script of plug-in \"") + this.name + "\".", exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ExaOperationEmulatorException(
                    "Script \"" + method + "\" of plug-in \"" + this.name + "\" got interrupted.");
        }
    }

    /**
     * Return list of functions provided by the plugin
     *
     * @return List as returned by the plugin. Usually in format "NAME: description"
     */
    public List<String> listFunctions() {
        ExecResult tmp = callFunctionInternal( "--show-functions", null );
        if( 0!=tmp.getExitCode() ) {
            throw new ExaOperationEmulatorException(
                    "--show-functions of plug-in \"" + this.name + "\" failed; error output:\n " + tmp.getStderr() );
        }

        return Arrays.asList( tmp.getStdout().split( "\n" ) );
    }


    /**
     * Start the plug-in.
     *
     * @return result of executing the start script.
     */
    // [impl->dsn~starting-plug-ins~1]
    public ExecResult start() {
        return callFunction("start");
    }

    /**
     * Stop the plug-in.
     *
     * @return result of executing the stop script.
     */
    // [impl->dsn~stopping-plug-ins~1]
    public ExecResult stop() {
        return callFunction("stop");
    }

    /**
     * Restart the plug-in.
     *
     * @return result of executing the restart script.
     */
    // [impl->dsn~restarting-plug-ins~1]
    public ExecResult restart() {
        return callFunction("restart");
    }

    /**
     * Get the status of the plug-in.
     *
     * @return result of executing the status script.
     */
    // [impl->dsn~getting-the-plug-ins-status~1]
    public ExecResult status() {
        return callFunction("status");
    }

    /**
     * Uninstall the plug-in.
     *
     * @return result of executing the removal script.
     */
    // [impl->dsn~uninstalling-plug-ins~1]
    public ExecResult uninstall() {
        return callFunction("uninstall");
    }
}
