package com.exasol.drivers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("fast")
class JdbcDriverTest {
    @Test
    void testGetName() {
        assertThat(JdbcDriver.builder("the_name").prefix("jdbc:ignore").mainClass("ignore").build().getName(),
                equalTo("the_name"));
    }

    @Test
    void testGetPrefix() {
        assertThat(JdbcDriver.builder("ignore").prefix("jdbc:the_prefix").mainClass("ignore").build().getPrefix(),
                equalTo("jdbc:the_prefix"));
    }

    @Test
    void testHasSourceFileFalse() {
        assertThat(JdbcDriver.builder("ignore").prefix("jdbc:ignore").mainClass("ignore").build().hasSourceFile(),
                equalTo(false));
    }

    @Test
    void testHasSourceFileTrue() {
        assertThat(
                JdbcDriver.builder("ignore").prefix("jdbc:ignore").mainClass("ignore").sourceFile(Path.of("/any/path"))
                        .build().hasSourceFile(), //
                equalTo(true));
    }

    @Test
    void testGetSourcePath() {
        final Path expectedPath = Path.of("/the/path");
        assertThat(
                JdbcDriver.builder("ignore").prefix("jdbc:ignore").mainClass("ignore").sourceFile(expectedPath).build()
                        .getSourcePath(), //
                equalTo(expectedPath));
    }

    @Test
    void testGetFileName() {
        final Path expectedPath = Path.of("/the/path/driver_1.0.0.jar");
        assertThat(
                JdbcDriver.builder("ignore").prefix("jdbc:ignore").mainClass("ignore").sourceFile(expectedPath).build()
                        .getFileName(), //
                equalTo("driver_1.0.0.jar"));
    }

    @Test
    void testGetSecurityManagerEnabledByDefault() {
        assertThat(
                JdbcDriver.builder("ignore").prefix("jdbc:ignore").mainClass("ignore").build()
                        .isSecurityManagerEnabled(), //
                equalTo(true));
    }

    @Test
    void testSecurityManagerDisabled() {
        assertThat(
                JdbcDriver.builder("ignore").prefix("jdbc:ignore").mainClass("ignore").enableSecurityManager(false)
                        .build().isSecurityManagerEnabled(), //
                equalTo(false));
    }

    @Test
    void testToString() {
        assertThat(JdbcDriver //
                .builder("the_name") //
                .prefix("jdbc:the_prefix") //
                .mainClass("com.example.Driver") //
                .build()//
                .toString(), //
                equalTo("JDBC driver \"the_name\" (com.example.Driver)"));
    }

    @Test
    void testToStringWithSourceFile() {
        assertThat(JdbcDriver //
                .builder("the_name") //
                .prefix("jdbc:the_prefix") //
                .sourceFile(Path.of("/the/path")) //
                .mainClass("com.example.Driver") //
                .build() //
                .toString(), //
                equalTo("JDBC driver \"the_name\" (com.example.Driver), source: \"" //
                        + Paths.get("/the/path") + "\""));
    }

    @Test
    void testGetManifest() {
        assertThat(JdbcDriver //
                .builder("another_name") //
                .prefix("jdbc:another_prefix") //
                .sourceFile(Path.of("/another/path/driver-file.jar")) //
                .mainClass("org.example.Driver").build().getManifest(),
                equalTo("DRIVERNAME=another_name\n" //
                        + "JAR=driver-file.jar\n" //
                        + "DRIVERMAIN=org.example.Driver\n" //
                        + "PREFIX=jdbc:another_prefix\n" //
                        + "NOSECURITY=NO\n" //
                        + "FETCHSIZE=100000\n" //
                        + "INSERTSIZE=-1\n"));
    }

    @Test
    void testGetManifestWithDisabledSecurityManager() {
        assertThat(JdbcDriver //
                .builder("another_name") //
                .prefix("jdbc:yet_another_prefix") //
                .sourceFile(Path.of("/another/path")) //
                .enableSecurityManager(false) //
                .mainClass("org.example.Driver") //
                .build().getManifest(), //
                containsString("NOSECURITY=YES"));
    }

    @CsvSource({ //
            "          , jdbc:foo:, com.example.Driver, Empty or illegal driver name (null)", //
            "'  '      , jdbc:foo:, com.example.Driver, Empty or illegal driver name (  )", //
            "The driver, jdbc:foo:, com.example.Driver, Empty or illegal driver name (The driver)", //
            "_driver   , jdbc:foo:, com.example.Driver, Empty or illegal driver name (_driver)", //
            "4driver   , jdbc:foo:, com.example.Driver, Empty or illegal driver name (4driver)", //
            "The_driver,          , com.example.Driver, Empty or illegal JDBC URL prefix (null)", //
            "The_driver, '  '     , com.example.Driver, Empty or illegal JDBC URL prefix (  )", //
            "The_driver, jdbc     , com.example.Driver, Empty or illegal JDBC URL prefix (jdbc)", //
            "The_driver, jdbc/foo , com.example.Driver, Empty or illegal JDBC URL prefix (jdbc/foo)", //
            "The_driver, jdbc:foo:,                   , Empty or illegal main class (null)", //
            "The_driver, jdbc:foo:, '  '              , Empty or illegal main class (  )", //
            "The_driver, jdbc:foo:, 4class            , Empty or illegal main class (4class)", //
            "The_driver, jdbc:foo:, com..ex.Driver    , Empty or illegal main class (com..ex.Driver)", //
    })
    @ParameterizedTest
    void testMissingMainClassThrowsException(final String name, final String prefix, final String mainClass,
            final String expectedMessagePart) {
        final JdbcDriver.Builder builder = JdbcDriver.builder(name).prefix(prefix).mainClass(mainClass);
        assertBuilderValidationException(builder, expectedMessagePart);
    }

    private void assertBuilderValidationException(final JdbcDriver.Builder builder, final String expectedMessagePart) {
        final Throwable exception = assertThrows(IllegalStateException.class, builder::build);
        assertThat(exception.getMessage(), containsString(expectedMessagePart));
    }
}