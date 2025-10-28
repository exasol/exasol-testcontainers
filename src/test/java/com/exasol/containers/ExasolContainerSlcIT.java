package com.exasol.containers;

import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;

import com.exasol.containers.slc.ScriptLanguageContainer;
import com.exasol.containers.slc.ScriptLanguageContainer.Language;

@Tag("slow")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExasolContainerSlcIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExasolContainerSlcIT.class);
    private static final long TIMESTAMP = System.currentTimeMillis();

    @Test
    void installSlcFailsIfNoBucketFSPortExposed() {
        try (final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()) {
            final ScriptLanguageContainer slc = createSlc();
            container.withExposedPorts(8563).withScriptLanguageContainer(slc);
            final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, container::start);
            assertThat(exception.getMessage(), startsWith("E-ETC-43"));
        }
    }

    @Test
    @Order(1)
    // [itest->dsn~install-custom-slc~1]
    // [itest->dsn~install-custom-slc.url~1]
    // [itest->dsn~install-custom-slc.verify-checksum~1]
    void installSlc() {
        try (final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()) {
            final ScriptLanguageContainer slc = createSlc();
            container.withReuse(true).withScriptLanguageContainer(slc);
            final Duration startupDuration = measureDuration(container::start);
            LOGGER.info("First startup took {}", startupDuration);
            assertAll(() -> assertThat("First startup duration [s]", startupDuration.toSeconds(), greaterThan(20L)),
                    () -> assertPython310Slc(container, slc));
        }
    }

    @Test
    @Order(2)
    // [itest->dsn~install-custom-slc.only-if-required~1]
    void installationSkippedWhenAlreadyInstalled() {
        try (final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()) {
            final ScriptLanguageContainer slc = createSlc();
            container.withReuse(true).withScriptLanguageContainer(slc);
            final Duration startupDuration = measureDuration(container::start);
            LOGGER.info("Second startup took {}", startupDuration);
            assertAll(() -> assertThat("Second startup duration [s]", startupDuration.toSeconds(), lessThan(5L)),
                    () -> assertPython310Slc(container, slc));
        }
    }

    Duration measureDuration(final Runnable runnable) {
        final Instant start = Instant.now();
        runnable.run();
        return Duration.between(start, Instant.now());
    }

    private ScriptLanguageContainer createSlc() {
        return ScriptLanguageContainer.builder() //
                .alias("MY_SLC_" + TIMESTAMP) //
                .language(Language.PYTHON) //
                .slcRelease("10.1.0",
                        "template-Exasol-all-python-3.12-release-I4FAKAOTX2WGHDOXJQCKISPWF66HDCJLSV2PJS26RZ454GBOUCUA.tar.gz") //
                .sha512sum(
                        "935b3d516e996f6d25948ba8a54c1b7f70f7f0e3f517e36481fdf0196c2c5cfc2841f86e891f3df9517746b7fb605db47cdded1b8ff78d9482ddaa621db43a34") //
                .build();
    }

    private void assertPython310Slc(final ExasolContainer<? extends ExasolContainer<?>> container,
            final ScriptLanguageContainer slc) throws SQLException {
        try (final Connection connection = container.createConnection();
                Statement statement = connection.createStatement()) {
            final String schemaName = "TEST_" + TIMESTAMP;
            statement.execute("create schema " + schemaName);
            final String udf = "CREATE " + slc.getAlias() + " SCALAR SCRIPT " + schemaName + ".get_python_version()\n" + //
                    "RETURNS VARCHAR(2000) AS\n" + //
                    "import sys\n" + //
                    "def run(ctx):\n" + //
                    "  return f\"{sys.version_info.major}.{sys.version_info.minor}\"\n" + //
                    "/";
            statement.execute(udf);
            assertThat(statement.executeQuery("select " + schemaName + ".get_python_version()"),
                    table().row("3.10").matches());
        }
    }
}
