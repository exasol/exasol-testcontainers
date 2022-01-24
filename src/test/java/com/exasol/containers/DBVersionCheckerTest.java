package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.ContainerLaunchException;

@Tag("fast")
class DBVersionCheckerTest {
    @ParameterizedTest
    @CsvSource({ //
            "aa.bb.cc,E-ETC-15", //
            "10.1.144.12,E-ETC-14", //
            "10.1,E-ETC-14", //
            "5.9.13,E-ETC-13", //
            "6.2.13,E-ETC-13", //
            "5.1.0,E-ETC-13" //
    })
    void testAssertBadParses(String dbVersionStr, String exceptionIdentifier) {
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class,
                () -> DBVersionChecker.minimumSupportedDbVersionCheck(dbVersionStr));
        assertThat(exception.getMessage(), containsString(exceptionIdentifier));
    }

    @ParameterizedTest
    @ValueSource(strings = { "6.3.0", "7.2.4" })
    void testAssertDoesntThrowValidVersion(String dbVersionStr) {
        assertDoesNotThrow(() -> DBVersionChecker.minimumSupportedDbVersionCheck(dbVersionStr));
    }
}