package com.exasol.exaoperation.plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.exec.ExitCode;
import com.exasol.containers.ssh.SshException;
import com.exasol.exaoperation.ExaOperationEmulatorException;

/**
 * Representation of an EXAoperation plug-in.
 * <p>
 * These plug-ins are installed from a plug-in package, an archive file with a defined structure and contain scripts or
 * executables called in the various life cycle phases of the plug-in (e.g. for installation, start and stop).
 * </p>
 */
public class Plugin {
    /** Plugin package prefix */
    public static final String PLUGIN_PACKAGE_PREFIX = "Plugin.";
    private static final Logger LOGGER = LoggerFactory.getLogger(Plugin.class);
    @SuppressWarnings("squid:S4784") // This is a test framework RegEx DoS attacks are unrealistic since this would mean
                                     // the testers are attacking themselves.
    private static final Pattern PLUGIN_PACKAGE_FILENAME_PATTERN = Pattern
            .compile("Plugin\\.([-.\\w]+?-\\d+(?:\\.\\d{1,20}){1,20}).*");
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
     * <p>
     * Plugins should handle function names case-insensitive.
     * </p>
     *
     * @param method name of the function to call.
     * @return result of the function call
     */
    public ExecResult callFunction(final String method) {
        return callFunction(method, "");
    }

    /**
     * Call one of the plugin's functions with the given argument.
     * <p>
     * Plugins should handle function names case-insensitive; content and encoding of argument depend on plugin and
     * called function.
     * </p>
     *
     * @param method   name of the function to call.
     * @param argument argument to the function call.
     * @return result of the function call
     */
    public ExecResult callFunction(final String method, final String argument) {
        if (null == argument) {
            throw new ExaOperationEmulatorException("Argument of Plugin::callFunction must never be null!");
        }
        return callFunctionInternal(method, argument);
    }

    /**
     * Internal implementation of callFunction, distinguishing call with and without argument.
     *
     * @param method   Name of function to call
     * @param argument optional single argument for the function call
     * @return result of function call
     */
    private ExecResult callFunctionInternal(final String method, final String... argument) {
        if (argument.length > 1) {
            throw new ExaOperationEmulatorException(
                    "Internal error: Multiple arguments provided when calling function \"" + method + "\" of plugin \""
                            + this.name + "\"");
        }

        try {
            final String script = "/usr/opt/EXAplugins/" + this.name + "/exaoperation-gate/plugin-functions";
            LOGGER.debug("Calling function \"{}\" of plug-in \"{}\".", method, this.name);
            if (argument.length == 1) {
                return this.container.execInContainer(script, method, argument[0]);
            } else {
                return this.container.execInContainer(script, method);
            }
        } catch (UnsupportedOperationException | SshException | IOException exception) {
            throw new ExaOperationEmulatorException(
                    "Unable to run \"" + method + ("\" script of plug-in \"") + this.name + "\".", exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ExaOperationEmulatorException(
                    "Script \"" + method + "\" of plug-in \"" + this.name + "\" got interrupted.");
        }
    }

    /**
     * Return list of functions provided by the plug-in.
     *
     * @return List as returned by the plug-in. Usually in format "NAME: description"
     */
    // [impl-dsn~listing-plug-ins~1]
    public List<String> listFunctions() {
        final ExecResult result = callFunctionInternal("--show-functions");
        if (result.getExitCode() != ExitCode.OK) {
            throw new ExaOperationEmulatorException(
                    "--show-functions of plug-in \"" + this.name + "\" failed; error output:\n " + result.getStderr());
        }

        final List<String> functions = new ArrayList<>();
        for (final String line : result.getStdout().split("\n")) {
            // due to what seems like a race condition in testcontainers, we need to filter out random empty lines...
            if (line.trim().isEmpty()) {
                continue;
            }
            functions.add(line);
        }
        return functions;
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