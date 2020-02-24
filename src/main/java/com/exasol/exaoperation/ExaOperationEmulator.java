package com.exasol.exaoperation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
    private static final String TMP_DIRECTORY = System.getProperty("java.io.tmpdir");
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
            copyPackageToContainer(plugin, TMP_DIRECTORY + "/" + plugin.getFileName());
            extractPluginPackage(plugin);
            registerPlugin(plugin);
            return plugin;
        } else {
            throw new IllegalArgumentException("Plug-in package \"" + sourcePath + "\" does not exist.");
        }
    }

    private void copyPackageToContainer(final Plugin plugin, final String targetPath) {
        final MountableFile file = MountableFile.forHostPath(plugin.getSourcePath());
        this.container.copyFileToContainer(file, targetPath);
    }

    private void extractPluginPackage(final Plugin plugin) {
        final String from = TMP_DIRECTORY + "/" + plugin.getFileName();
        final String to = TMP_DIRECTORY + "/";
        LOGGER.debug("Extracting EXAoperation plug-in from \"{}\" to \"{}\".", from, to);
        try {
            final ExecResult unpackOuterResult = this.container.execInContainer("tar", "xf", from, "-C", to);
            if (unpackOuterResult.getExitCode() == ExitCode.OK) {
                extractPluginPayload(plugin);
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

    private void extractPluginPayload(final Plugin plugin) throws IOException, InterruptedException {
        final String from = assembleInnerPath(plugin);
        final String to = "/";
        LOGGER.debug("Extracting the plug-in's inner archive \"{}\" to \"{}\".", from, to);
        final ExecResult unpackInnerResult = this.container.execInContainer("tar", "xzf", from, "-C", to);
        if (unpackInnerResult.getExitCode() != ExitCode.OK) {
            throw new ExaOperationEmulatorException(
                    "Failed to unpack plug-in payload (inner TAR archive).\nCause: " + unpackInnerResult.getStderr());
        }
    }

    private String assembleInnerPath(final Plugin plugin) {
        return TMP_DIRECTORY + "/" + Plugin.PLUGIN_PACKAGE_PREFIX + plugin.getName() + ".tar.gz";
    }

    private void registerPlugin(final Plugin plugin) {
        this.plugins.put(plugin.getName(), plugin);
    }

    @Override
    public boolean hasPlugin(final String pluginName) {
        return this.plugins.containsKey(pluginName);
    }
}