package com.exasol.containers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ExasolContainerProviderTest {
    private ExasolContainerProvider provider;

    @BeforeEach
    void beforeEach() {
        provider = new ExasolContainerProvider();
    }

    @CsvSource({ "exasol,true", "acme,false", "\"\",false" })
    @ParameterizedTest
    void testSupports(final String databaseType, final boolean expectedToBeSupported) throws Exception {
        assertThat(provider.supports(databaseType), equalTo(expectedToBeSupported));
    }

    @Test
    void testNewInstance() throws Exception {
        assertThat(provider.newInstance(), instanceOf(ExasolContainer.class));
    }
}