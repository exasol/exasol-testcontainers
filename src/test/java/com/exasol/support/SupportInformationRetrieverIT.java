package com.exasol.support;

import static com.exasol.containers.ExasolService.UDF;
import static com.exasol.containers.ExitType.*;
import static com.exasol.support.SupportInformationRetriever.MONITORED_EXIT_PROPERTY;
import static com.exasol.support.SupportInformationRetriever.TARGET_DIRECTORY_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.*;

@Tag("slow")
@Testcontainers
class SupportInformationRetrieverIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportInformationRetrieverIT.class);
    private static final String SYSINFO_FILENAME = "sysinfo.txt";

    // [itest->dsn~configure-support-information-retriever-via-api~1]
    @Test
    void testWriteSupportBundleOnExit(@TempDir final Path tempDir) {
        unsetControlProperties();
        try (final ExasolContainer<? extends ExasolContainer<?>> exasol = new ExasolContainer<>()) {
            exasol.withRequiredServices() //
                    .withSupportInformationRecordedAtExit(tempDir, EXIT_ANY) //
                    .start();
            exasol.stop();
        }
        assertTarArchiveContainsEntry(getHostSupportBundlePath(tempDir), SYSINFO_FILENAME);
    }

    private void unsetControlProperties() {
        System.clearProperty(TARGET_DIRECTORY_PROPERTY);
        System.clearProperty(MONITORED_EXIT_PROPERTY);
    }

    private void assertTarArchiveContainsEntry(final Path pathToArchive, final String entryFragment) {
        boolean present = false;
        try ( //
                final GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(pathToArchive.toString()));
                final TarArchiveInputStream tar = new TarArchiveInputStream(gzip) //
        ) {
            ArchiveEntry entry = tar.getNextEntry();
            while (entry != null) {
                if (entry.getName().contains(entryFragment)) {
                    present = true;
                    break;
                }
                entry = tar.getNextEntry();
            }
        } catch (final IOException exception) {
            throw new AssertionError("Unable to check entry '" + entryFragment + "' in archive '" + pathToArchive
                    + "'. Cause: " + exception, exception);
        }
        assertThat("Entry '" + entryFragment + "' present in archive '" + pathToArchive + "'", present, is(true));
    }

    private Path getHostSupportBundlePath(final Path parentDirectory) {
        final String filename = findSupportArchive(parentDirectory);
        final Path path = parentDirectory.resolve(filename);
        LOGGER.info("Support bundle archive path: {}", path);
        return path;
    }

    private String findSupportArchive(final Path directory) {
        final String[] files = directory.toFile().list();
        for (final String filename : files) {
            if (filename.startsWith(SupportInformationRetriever.SUPPORT_ARCHIVE_PREFIX)) {
                return filename;
            }
        }
        throw new AssertionError("Unable to find archive file in directory '" + directory + "'. Directory contains "
                + files.length + " files: " + Arrays.toString(files));
    }

    // [itest->dsn~configure-support-information-retriever-via-system-properties~1]
    @Test
    void testWriteSupportBundleOnExitConfiguredByProperty(@TempDir final Path tempDir) {
        System.setProperty(TARGET_DIRECTORY_PROPERTY, tempDir.toString());
        System.setProperty(MONITORED_EXIT_PROPERTY, EXIT_SUCCESS.toString());
        try (final ExasolContainer<? extends ExasolContainer<?>> exasol = new ExasolContainer<>()) {
            exasol.withRequiredServices().start();
            exasol.stop();
        }
        assertTarArchiveContainsEntry(getHostSupportBundlePath(tempDir), SYSINFO_FILENAME);
    }

    // [itest->dsn~support-information-retriever-creates-support-archive-depending-on-exit-type~1]
    @Test
    void testArchiveProducedWhenExitWithErrorTriggerIsConfigured(@TempDir final Path tempDir) {
        unsetControlProperties();
        final boolean failed = runContainerSupposedToFailAtStartup(tempDir, EXIT_ERROR);
        if (failed) {
            assertTarArchiveContainsEntry(getHostSupportBundlePath(tempDir), SYSINFO_FILENAME);
        } else {
            fail("Since container startup is supposed to fail, this line should not be reached!");
        }
    }

    private boolean runContainerSupposedToFailAtStartup(final Path tempDir, final ExitType exitType) {
        boolean exceptionThrown = false;
        try (final WaitFailSimulationContainer<? extends WaitFailSimulationContainer<?>> exasol = //
                new WaitFailSimulationContainer<>() //
        ) {
            unsetControlProperties();
            exasol.withRequiredServices(UDF) // fake container fails while wait for UDF (see below).
                    .withSupportInformationRecordedAtExit(tempDir, exitType) //
                    .start();
        } catch (final ContainerLaunchException exception) {
            exceptionThrown = true;
        }
        return exceptionThrown;
    }

    // [itest->dsn~support-information-retriever-creates-support-archive-depending-on-exit-type~1]
    @Test
    void testArchiveNotProducedWhenExitWithSuccessTriggerIsConfigured(@TempDir final Path tempDir) {
        unsetControlProperties();
        final boolean failed = runContainerSupposedToFailAtStartup(tempDir, EXIT_SUCCESS);
        if (failed) {
            assertDirectoryEmpty(tempDir);
        } else {
            fail("Since container startup is supposed to fail, this line should not be reached!");
        }
    }

    private void assertDirectoryEmpty(final Path directory) {
        assertThat(directory.toFile().listFiles(), emptyArray());
    }

    private static class WaitFailSimulationContainer<T extends WaitFailSimulationContainer<T>>
            extends ExasolContainer<T> {
        @Override
        protected void waitForUdfContainer() {
            super.waitForUdfContainer();
            // We need the timeout late in the upstart process so that at least the facilities that exasupport needs are
            // available.
            LOGGER.info("Injecting fake ContainerLaunchException");
            throw new ContainerLaunchException("Fake timeout");
        }
    }
}
