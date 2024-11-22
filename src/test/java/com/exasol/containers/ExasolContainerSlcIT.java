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

import com.exasol.containers.slc.ScriptLanguageContainer;
import com.exasol.containers.slc.ScriptLanguageContainer.Language;
import org.testcontainers.containers.ContainerLaunchException;

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
            ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, container::start);
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
            assertAll(() -> assertThat(startupDuration.toSeconds(), greaterThan(45L)),
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
            assertAll(() -> assertThat(startupDuration.toSeconds(), lessThan(5L)),
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
                .slcRelease("7.1.0", "template-Exasol-all-python-3.10_release.tar.gz") //
                .sha512sum(
                        "db19e16b9cb5b3d02c44ad6e401eb9296b0483c0078a2e23ac00ad7f26473d115febb0c799a86aed33f49252947a86aa7e8927571a2679131da52e5fc090939c") //
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
