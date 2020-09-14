package com.exasol.containers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test an Exasol Container with version lower than 7.0.
 */
public class ExasolContainerBackwardCompatibilityTest {
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>("6.2.7-d1");

    @Test
    void testBucketfsPort() {
        assertThat(CONTAINER.getDefaultInternalBucketfsPort(), equalTo(6583));
    }

    @Test
    void testDatabasePort() {
        assertThat(CONTAINER.getDefaultInternalDatabasePort(), equalTo(8888));
    }
}
