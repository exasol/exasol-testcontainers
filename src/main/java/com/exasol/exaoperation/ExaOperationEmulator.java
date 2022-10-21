package com.exasol.exaoperation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.exec.ExitCode;
import com.exasol.containers.ssh.SshException;
import com.exasol.exaoperation.plugin.Plugin;

/**
 * Emulator that provides a subset of EXAoperation features needed for integration testing.
 */
public class ExaOperationEmulator implements ExaOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExaOperationEmulator.class);
    private final ExasolContainer<? extends ExasolContainer<?>> container;
    private final Map<String, Plugin> plugins = new HashMap<>();

    /**
     * Create a new instance of the {@link ExaOperationEmulator}.
     *
     * @param container parent container
     */
    public ExaOperationEmulator(final ExasolContainer<? extends ExasolContainer<?>> container) {
        this.container = container;
    }

    /**
     * Wrapped handler for `Container.execInContainer`, performing all the local ExitCode and Exception handling.
     * <p>
     * Will also throw an exception if the given command fails with a regular exitcode based error.
     * </p>
     *
     * @param description Text identifying the operation; used as prefix for all thrown Exceptions
     * @param command     Command to be executed in container
     * @return The result of container execution, in case of success
     */
    private ExecResult execInContainer(final String description, final String... command) {
        try {
            final ExecResult result = this.container.execInContainer(command);
            if (result.getExitCode() != ExitCode.OK) {
                throw new ExaOperationEmulatorException(
                        description + " in container not successful: " + result.getStderr());
            }
            return result;
        } catch (final InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw new ExaOperationEmulatorException(description + " in container got interrupted.");
        } catch (UnsupportedOperationException | SshException | IOException exception) {
            throw new ExaOperationEmulatorException(description + " in container failed.", exception);
        }
    }

    // [impl->dsn~extracting-plug-in-packages~1]
    @Override
    public Plugin installPluginPackage(final Path sourcePath) {
        if (sourcePath.toFile().exists()) {
            try {
                LOGGER.debug("Installing plug-in \"{}\".", sourcePath);
                final Plugin plugin = new Plugin(sourcePath, this.container);
                if (hasPlugin(plugin.getName())) {
                    throw new ExaOperationEmulatorException(
                            "Plugin \"" + plugin.getName() + "\" is already installed.");
                }

                final String tmpDirectory = createTempDirectory();
                copyPackageToContainer(plugin, tmpDirectory + "/" + plugin.getFileName());
                extractPluginPackage(plugin, tmpDirectory);
                registerPlugin(plugin);
                removeTempDirectory(tmpDirectory);
                return plugin;
            } catch (final ExaOperationEmulatorException exception) {
                throw new ExaOperationEmulatorException("Unable to install plug-in.", exception);
            }
        } else {
            throw new IllegalArgumentException("Plug-in package \"" + sourcePath + "\" does not exist.");
        }
    }

    private String createTempDirectory() {
        final ExecResult result = execInContainer("Create temp directory for plugin", "/bin/mktemp", "--directory",
                "--tmpdir=/tmp", "tmp.XXXXXXXX-plugin");
        return result.getStdout().trim();
    }

    private void removeTempDirectory(final String tempDirectory) {
        if (!Pattern.compile("/tmp/.+-plugin").matcher(tempDirectory).matches()) {
            // just to be on the safe side. Chances are slim that a bad path comes in here.
            throw new ExaOperationEmulatorException(
                    "\"" + tempDirectory + "\" does not meet our naming convention. Refusing to delete.");
        }
        execInContainer("Remove plugin temp directory", "/bin/rm", "-rf", tempDirectory);
    }

    private void copyPackageToContainer(final Plugin plugin, final String targetPath) throws SshException {
        this.container.copyFileToContainer(plugin.getSourcePath(), targetPath);
    }

    private void extractPluginPackage(final Plugin plugin, final String tempDirectory) {
        final String from = tempDirectory + "/" + plugin.getFileName();
        final String to = tempDirectory + "/";
        LOGGER.debug("Extracting EXAoperation plug-in from \"{}\" to \"{}\".", from, to);
        execInContainer("Extract plugin package " + plugin.getSourcePath().getFileName(), "tar", "xf", from, "-C", to);
        extractPluginPayload(plugin, tempDirectory);
    }

    private void extractPluginPayload(final Plugin plugin, final String tempDirectory) {
        final String from = assembleInnerPath(plugin, tempDirectory);
        final String to = "/";
        LOGGER.debug("Extracting the plug-in's inner archive \"{}\" to \"{}\".", from, to);
        execInContainer("Unpack plug-in payload (inner TAR archive)", "tar", "xzf", from, "-C", to);
    }

    private String assembleInnerPath(final Plugin plugin, final String tempDirectory) {
        return tempDirectory + "/" + Plugin.PLUGIN_PACKAGE_PREFIX + plugin.getName() + ".tar.gz";
    }

    private void registerPlugin(final Plugin plugin) {
        this.plugins.put(plugin.getName(), plugin);
    }

    @Override
    public boolean hasPlugin(final String pluginName) {
        return this.plugins.containsKey(pluginName);
    }

    @Override
    public Plugin getPlugin(final String pluginName) {
        if (hasPlugin(pluginName)) {
            return this.plugins.get(pluginName);
        } else {
            throw new IllegalArgumentException("Unable to get control object for plug-in named \"" + pluginName
                    + "\". Choose one of: " + String.join(", ", getPluginNames()));
        }
    }

    // [impl->dsn~listing-plug-ins~1]
    @Override
    public List<String> getPluginNames() {
        return new ArrayList<>(this.plugins.keySet());
    }

}