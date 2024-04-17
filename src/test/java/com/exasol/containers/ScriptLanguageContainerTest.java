package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.exasol.containers.slc.ScriptLanguageContainer;
import com.exasol.containers.slc.ScriptLanguageContainer.Builder;
import com.exasol.containers.slc.ScriptLanguageContainer.Language;
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
    void builderReturnsNewInstances() {
        final Builder builder = ScriptLanguageContainer.builder().language(Language.JAVA).alias("java17")
                .localFile(Path.of("test"));
        final ScriptLanguageContainer container1 = builder.build();
        final ScriptLanguageContainer container2 = builder.build();
        assertThat(container2, not(sameInstance(container1)));
    }
}
