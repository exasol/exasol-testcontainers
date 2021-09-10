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
    static final String SUPPORT_ARCHIVE_PREFIX = "exacluster_debuginfo_";
    private static final String EXASUPPORT_EXECUTABLE = "exasupport";
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportInformationRetriever.class);
    private static final String MAPPED_HOST_DIRECTORY = "/exa/tmp/support";
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
        LOGGER.debug("Monitoring exit type {}, writing to target dir {}", this.monitoredExitType, this.targetDirectory);
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
     * @param exitType type of exit that occurred
     */
    // [impl->dsn~support-information-retriever-creates-support-archive-depending-on-exit-type~1]
    public void run(final ExitType exitType) {
        if ((this.monitoredExitType == ExitType.EXIT_ANY) || (exitType == this.monitoredExitType)) {
            createArchiveBundle(exitType);
        } else {
            LOGGER.debug("Skipping support package creation. Exit type is {}, monitoring {}", exitType,
                    this.monitoredExitType);
        }
    }

    @SuppressWarnings("java:S112")
    private void createArchiveBundle(final ExitType exitType) {
        try {
            final ExecResult result = this.container.execInContainer(EXASUPPORT_EXECUTABLE);
            if (result.getExitCode() == ExitCode.OK) {
                final String filename = extractFilenameFromConsoleMessage(result);
                final String hostPath = getHostPath(filename);
                logSuccessfulArchiveCreationAttempt(exitType, hostPath);
            } else {
                logFailedSupportArchiveCreationAttempt(exitType, result.getStderr());
            }
        } catch (final UnsupportedOperationException | IOException exception) {
            logFailedSupportArchiveCreationAttempt(exitType, exception.getMessage());
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(exception);
        }
    }

    @SuppressWarnings("java:S2629")
    private void logSuccessfulArchiveCreationAttempt(final ExitType exitType, final String pathOfArchiveOnHost) {
        LOGGER.info("Container exiting with {}. Monitoring is set to {}. Wrote support archive to: {}", exitType,
                this.monitoredExitType, pathOfArchiveOnHost);
    }

    private String extractFilenameFromConsoleMessage(final ExecResult result) {
        final String consoleMessage = result.getStdout().strip();
        return consoleMessage.substring(consoleMessage.indexOf(SUPPORT_ARCHIVE_PREFIX));
    }

    private String getHostPath(final String filename) {
        return this.targetDirectory.resolve(filename).toString();
    }

    @SuppressWarnings("java:S2629")
    private void logFailedSupportArchiveCreationAttempt(final ExitType exitType, final String cause) {
        LOGGER.error(ExaError.messageBuilder("E-ETC-2") //
                .message("Container exiting with {}. Monitoring is set to {}. Unable to create support archive."
                        + "\nCause: {{cause}}", exitType, this.monitoredExitType, this.targetDirectory, cause) //
                .toString());
    }
}
