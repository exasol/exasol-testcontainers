package com.exasol.containers.slc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class SlcConfigurationTest {

    @ParameterizedTest
    @ValueSource(strings = { "", "x=y", "x=y a=b", "x=y a=b c=d", "x=y=a b=c",
            "R=builtin_r JAVA=builtin_java PYTHON3=builtin_python3 MY_SLC=localzmq+protobuf:///bfsdefault/default/template-Exasol-all-python-3.10_release?lang=python#buckets/bfsdefault/default/template-Exasol-all-python-3.10_release//exaudf/exaudfclient_py3" })
    void parseAndFormat(final String value) {
        final SlcConfiguration config = SlcConfiguration.parse(value);
        assertThat(config.format(), equalTo(value));
    }

    @ParameterizedTest
    @CsvSource({ "' ', ''", "' x=b ', x=b", "'\ty\t=\tb\t', y=b", "'\nz\n=\nb\n', z=b" })
    void trimsWhitespace(final String value, final String expected) {
        final SlcConfiguration config = SlcConfiguration.parse(value);
        assertThat(config.format(), equalTo(expected));
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = { //
            "x; E-ETC-28: Invalid entry in SLC configuration: 'x'",
            "=; E-ETC-29: Invalid key in SLC configuration: '='",
            "key=; E-ETC-30: Invalid value in SLC configuration: 'key='",
            "a=b x; E-ETC-28: Invalid entry in SLC configuration: 'x'",
            "a=b key=; E-ETC-30: Invalid value in SLC configuration: 'key='",
            "a=b key=; E-ETC-30: Invalid value in SLC configuration: 'key='",
            "a=b a=c; E-ETC-31: Found two entries with the same key: 'b', 'c'" })
    void parsingFails(final String value, final String expectedErrorMessage) {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> SlcConfiguration.parse(value));
        assertThat(exception.getMessage(), equalTo(expectedErrorMessage));
    }

    @Test
    void insertNewEntry() {
        final SlcConfiguration config = SlcConfiguration.parse("a=b");
        config.setAlias("c", "d");
        assertThat(config.format(), equalTo("a=b c=d"));
    }

    @Test
    void insertTrimsEntries() {
        final SlcConfiguration config = SlcConfiguration.parse("");
        config.setAlias(" a ", " b ");
        config.setAlias("\tc\t", "\td\t");
        config.setAlias("\ne\n", "\nf\n");
        assertThat(config.format(), equalTo("a=b c=d e=f"));
    }

    @Test
    void overwriteExistingEntry() {
        final SlcConfiguration config = SlcConfiguration.parse("a=b");
        config.setAlias("a", "x");
        assertThat(config.format(), equalTo("a=x"));
    }
}
