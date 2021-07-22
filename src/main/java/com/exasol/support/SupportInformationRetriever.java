package com.exasol.support;

import static org.testcontainers.containers.BindMode.READ_WRITE;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.ExitType;
import com.exasol.containers.exec.ExitCode;
import com.exasol.errorreporting.ExaError;

/**
 * Manages getting support information (like cluster logs, configuration settings and core-dumps) from the database.
 */
public class SupportInformationRetriever {
    public static final String TARGET_DIRECTORY_PROPERTY = "com.exasol.containers.support_information_target_dir";
    public static final String MONITORED_EXIT_PROPERTY = "com.exasol.containers.monitored_exit";
    static final String BUNDLE_ARCHIVE_FILENAME = "exasol_support_bundle.tar.gz";
    private static final String EXASUPPORT_EXECUTABLE = "exasupport";
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportInformationRetriever.class);
    private static final String MAPPED_HOST_DIRECTORY = "/exa/tmp/support/mapped_host_dir";
    private static final ExitType DEFAULT_MONITORED_EXIT_TYPE = ExitType.EXIT_NONE;
    private final Container<? extends Container<?>> container;
    private Path targetDirectory;
    private ExitType monitoredExitType;

    /**
     * Create a new instance of a {@link SupportInformationRetriever}.
     *
     * @param container container from which to extract the support information
     */
    public SupportInformationRetriever(final Container<? extends Container<?>> container) {
        this.container = container;
        final String monitoredExitPropertyValue = System.getProperty(MONITORED_EXIT_PROPERTY);
        getSettingsFromPropertiesOrDefaultValues(monitoredExitPropertyValue);
    }

    // [impl->dsn~configure-support-information-retriever-via-system-properties~1]
    private void getSettingsFromPropertiesOrDefaultValues(final String monitoredExitPropertyValue) {
        monitorExit((monitoredExitPropertyValue == null) ? DEFAULT_MONITORED_EXIT_TYPE
                : ExitType.valueOf(monitoredExitPropertyValue));
        final String targetDirectoryPropertyValue = System.getProperty(TARGET_DIRECTORY_PROPERTY);
        if (targetDirectoryPropertyValue != null) {
            mapTargetDirectory(Path.of(targetDirectoryPropertyValue));
        }
    }

    /**
     * Map a host directory as target directory for the support bundle.
     *
     * @param targetDirectory host directory in which to create the support bundle
     */
    // [impl->dsn~configure-support-information-retriever-via-api~1]
    public void mapTargetDirectory(final Path targetDirectory) {
        this.targetDirectory = targetDirectory;
        this.container.withFileSystemBind(targetDirectory.toString(), MAPPED_HOST_DIRECTORY, READ_WRITE);
    }

    /**
     * Set which type of exit should produce the support bundle.
     *
     * @param exitType type of exit for which a support bundle is created
     */
    // [impl->dsn~configure-support-information-retriever-via-api~1]
    public void monitorExit(final ExitType exitType) {
        this.monitoredExitType = exitType;
    }

    /**
     * Produce the support information bundle archive in the mapped directory.
     *
     * @param exitType
     */
    // [impl->req~exit-dependent-support-archive-generation~1]
    public void run(final ExitType exitType) {
        if ((this.monitoredExitType == ExitType.EXIT_ANY) || (exitType == this.monitoredExitType)) {
            createArchiveBundle(exitType);
        }
    }

    @SuppressWarnings("java:S112")
    private void createArchiveBundle(final ExitType exitType) {
        try {
            LOGGER.info(
                    "Container exiting with {}. Monitoring is set to {}."
                            + " Writing support information archive '{}' to mapped host directory '{}'",
                    exitType, this.monitoredExitType, BUNDLE_ARCHIVE_FILENAME, this.targetDirectory);
            final ExecResult result = this.container.execInContainer(EXASUPPORT_EXECUTABLE, "-o",
                    getContainerSupportBundlePath());
            if (result.getExitCode() != ExitCode.OK) {
                final String message = ExaError.messageBuilder("E-ETC-2") //
                        .message("exasupport exited with code {{exit-code}}.\n{{stderr-output}}", result.getExitCode(),
                                result.getStderr()) //
                        .toString();
                LOGGER.error(message);
            }
        } catch (final UnsupportedOperationException | IOException exception) {
            LOGGER.error(ExaError.messageBuilder("E-ETC-1")
                    .message("Unable to create support bundle archive. Cause: {{cause}}", exception.getMessage())
                    .toString());
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(exception);
        }
    }

    private String getContainerSupportBundlePath() {
        return MAPPED_HOST_DIRECTORY + "/" + BUNDLE_ARCHIVE_FILENAME;
    }
}