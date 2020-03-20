package com.exasol.exaoperation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.utility.MountableFile;

import com.exasol.containers.exec.ExitCode;
import com.exasol.exaoperation.plugin.Plugin;

/**
 * Emulator that provides a subset of EXAoperations features needed for integration testing.
 */
public class ExaOperationEmulator implements ExaOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExaOperationEmulator.class);
    private final Container<? extends Container<?>> container;
    private final Map<String, Plugin> plugins = new HashMap<>();

    /**
     * Create a new instance of the {@link ExaOperationEmulator}.
     *
     * @param container parent container
     */
    public ExaOperationEmulator(final Container<? extends Container<?>> container) {
        this.container = container;
    }

    // [impl->dsn~extracting-plug-in-packages~1]
    @Override
    public Plugin installPluginPackage(final Path sourcePath) {
        if (sourcePath.toFile().exists()) {
            LOGGER.info("Installing plug-in \"{}\".", sourcePath);
            final Plugin plugin = new Plugin(sourcePath, this.container);
            if (hasPlugin(plugin.getName())) {
                throw new ExaOperationEmulatorException("Plugin \"" + plugin.getName() + "\" is already installed.");
            }

            String tmpDirectory = createTempDirectory();
            copyPackageToContainer(plugin, tmpDirectory + "/" + plugin.getFileName());
            extractPluginPackage(plugin, tmpDirectory);
            registerPlugin(plugin);
            removeTempDirectory(tmpDirectory);
            return plugin;
        } else {
            throw new IllegalArgumentException("Plug-in package \"" + sourcePath + "\" does not exist.");
        }
    }

    private String createTempDirectory() {
        try {
            ExecResult result = container.execInContainer("/bin/mktemp", "--directory", "--tmpdir=/tmp",
                    "tmp.XXXXXXXX-plugin");
            if (result.getExitCode() != ExitCode.OK) {
                throw new ExaOperationEmulatorException(
                        "Failed creating temp directory for plugin: " + result.getStderr());
            }
            return result.getStdout().trim();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw new ExaOperationEmulatorException("Creation of temp directory in container got interrupted.");
        } catch (IOException exception) {
            throw new ExaOperationEmulatorException("Creation of temp directory in container failed.", exception);
        }
    }

    private void removeTempDirectory(final String tempDirectory) {
        if (!Pattern.compile("/tmp/.+-plugin").matcher(tempDirectory).matches()) {
            // just to be on the safe side. Chances are slim that a bad path comes in here.
            throw new ExaOperationEmulatorException(
                    "\"" + tempDirectory + "\" does not meet our naming convention. Refusing to delete.");
        }
        try {
            ExecResult result = container.execInContainer("/bin/rm", "-rf", tempDirectory);
            if (result.getExitCode() != ExitCode.OK) {
                throw new ExaOperationEmulatorException(
                        "Failed removing temp directory for plugin: " + result.getStderr());
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw new ExaOperationEmulatorException("Removal of temp directory in container got interrupted.");
        } catch (IOException exception) {
            throw new ExaOperationEmulatorException("Removal of temp directory in container failed.", exception);
        }
    }

    private void copyPackageToContainer(final Plugin plugin, final String targetPath) {
        final MountableFile file = MountableFile.forHostPath(plugin.getSourcePath());
        this.container.copyFileToContainer(file, targetPath);
    }

    private void extractPluginPackage(final Plugin plugin, final String tempDirectory) {
        final String from = tempDirectory + "/" + plugin.getFileName();
        final String to = tempDirectory + "/";
        LOGGER.debug("Extracting EXAoperation plug-in from \"{}\" to \"{}\".", from, to);
        try {
            final ExecResult unpackOuterResult = this.container.execInContainer("tar", "xf", from, "-C", to);
            if (unpackOuterResult.getExitCode() == ExitCode.OK) {
                extractPluginPayload(plugin, tempDirectory);
            } else {
                throw new ExaOperationEmulatorException("Failed to unpack plug-in package (outer TAR archive).\nCause: "
                        + unpackOuterResult.getStderr());
            }
        } catch (UnsupportedOperationException | IOException exception) {
            throw new ExaOperationEmulatorException(
                    "Unable to install plug-in from \"" + plugin.getSourcePath() + "\".", exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ExaOperationEmulatorException(
                    "Installation of plug-in \"" + plugin.getName() + "\" got interrupted.");
        }
    }

    private void extractPluginPayload(final Plugin plugin, final String tempDirectory)
            throws IOException, InterruptedException {
        final String from = assembleInnerPath(plugin, tempDirectory);
        final String to = "/";
        LOGGER.debug("Extracting the plug-in's inner archive \"{}\" to \"{}\".", from, to);
        final ExecResult unpackInnerResult = this.container.execInContainer("tar", "xzf", from, "-C", to);
        if (unpackInnerResult.getExitCode() != ExitCode.OK) {
            throw new ExaOperationEmulatorException(
                    "Failed to unpack plug-in payload (inner TAR archive).\nCause: " + unpackInnerResult.getStderr());
        }
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
                    + "\". Choose one of: " + String.join(", ", this.plugins.keySet()));
        }
    }
}