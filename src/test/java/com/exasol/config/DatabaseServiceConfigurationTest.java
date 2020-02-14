package com.exasol.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class DatabaseServiceConfigurationTest {
    @Test
    void testGetName() {
        final String expectedName = "DB3";
        final DatabaseServiceConfiguration config = DatabaseServiceConfiguration.builder().databaseName(expectedName).build();
        assertThat(config.getDatabaseName(), equalTo(expectedName));
    }
}