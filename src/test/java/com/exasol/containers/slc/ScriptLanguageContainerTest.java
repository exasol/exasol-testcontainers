package com.exasol.containers.slc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.exasol.containers.slc.ScriptLanguageContainer.Builder;
import com.exasol.containers.slc.ScriptLanguageContainer.Language;
import com.exasol.testutil.SerializableVerifier;
import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class ScriptLanguageContainerTest {
    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(ScriptLanguageContainer.class).verify();
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(ScriptLanguageContainer.class).verify();
    }

    @Test
    void testSerializable() {
        SerializableVerifier.assertSerializable(ScriptLanguageContainer.class, ScriptLanguageContainer.builder()
                .language(Language.JAVA).alias("java17").localFile(Path.of("test")).build());
    }

    @Test
    void builderReturnsNewInstances() {
        final Builder builder = ScriptLanguageContainer.builder().language(Language.JAVA).alias("java17")
                .localFile(Path.of("test"));
        final ScriptLanguageContainer container1 = builder.build();
        final ScriptLanguageContainer container2 = builder.build();
        assertThat(container2, not(sameInstance(container1)));
    }

    @ParameterizedTest
    @CsvSource({ "JAVA, JAVA", "R, R", "PYTHON, PYTHON3" })
    void defaultAlias(final Language language, final String expectedAlias) {
        final ScriptLanguageContainer container = ScriptLanguageContainer.builder().language(language)
                .localFile(Path.of("test")).build();
        assertThat(container.getAlias(), equalTo(expectedAlias));
    }

    @Test
    void customAlias() {
        final ScriptLanguageContainer container = ScriptLanguageContainer.builder().language(Language.PYTHON)
                .alias("custom_alias").localFile(Path.of("test")).build();
        assertThat(container.getAlias(), equalTo("custom_alias"));
    }

    @ParameterizedTest
    @CsvSource({ "JAVA, /exaudf/exaudfclient", "R, /exaudf/exaudfclient", "PYTHON, /exaudf/exaudfclient_py3" })
    void defaultUdfEntryPoint(final Language language, final String expectedEntryPoint) {
        final ScriptLanguageContainer container = ScriptLanguageContainer.builder().language(language)
                .localFile(Path.of("test")).build();
        assertThat(container.getUdfEntryPoint(), equalTo(expectedEntryPoint));
    }

    @Test
    void customUdfEntryPoint() {
        final ScriptLanguageContainer container = ScriptLanguageContainer.builder().language(Language.JAVA)
                .udfEntryPoint("/custom/udf_entry_point").localFile(Path.of("test")).build();
        assertThat(container.getUdfEntryPoint(), equalTo("/custom/udf_entry_point"));
    }
}
