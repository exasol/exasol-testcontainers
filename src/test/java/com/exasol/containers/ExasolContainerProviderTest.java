package com.exasol.containers;

import static com.exasol.containers.DockerImageReferenceFactory.versionFromSystemPropertyOrIndividual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("fast")
class ExasolContainerProviderTest {
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
        final String version = versionFromSystemPropertyOrIndividual("7.0.4");
        assertThat(this.provider.newInstance(version), instanceOf(ExasolContainer.class));
    }
}