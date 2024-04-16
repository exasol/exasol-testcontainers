package com.exasol.containers;

import org.junit.jupiter.api.Test;

import com.exasol.containers.slc.ScriptLanguageContainer;

import nl.jqno.equalsverifier.EqualsVerifier;

class ScriptLanguageContainerTest {
    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(ScriptLanguageContainer.class).verify();
    }
}
