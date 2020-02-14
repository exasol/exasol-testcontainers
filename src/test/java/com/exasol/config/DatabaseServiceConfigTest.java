package com.exasol.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class DatabaseServiceConfigTest {
    @Test
    void testGetName() {
        final String expectedName = "DB3";
        final DatabaseServiceConfig config = DatabaseServiceConfig.builder().databaseName(expectedName).build();
        assertThat(config.getDatabaseName(), equalTo(expectedName));
    }
}