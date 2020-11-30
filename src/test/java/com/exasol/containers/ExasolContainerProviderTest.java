package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ExasolContainerProviderTest {
    private ExasolContainerProvider provider;

    @BeforeEach
    void beforeEach() {
        this.provider = new ExasolContainerProvider();
    }

    @CsvSource({ "exasol,true", "acme,false", "\"\",false" })
    @ParameterizedTest
    void testSupports(final String databaseType, final boolean expectedToBeSupported) throws Exception {
        assertThat(this.provider.supports(databaseType), equalTo(expectedToBeSupported));
    }

    @Test
    void testNewInstance() throws Exception {
        assertThat(this.provider.newInstance("7.0.0"), instanceOf(ExasolContainer.class));
    }
}