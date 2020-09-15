package com.exasol.containers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test an Exasol Container with version lower than 7.0.
 */
public class ExasolContainerVersionSpecificTest {
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER_V6 = new ExasolContainer<>("6.2.7-d1");
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER_V7 = new ExasolContainer<>("7.0.0");

    @Test
    void testBucketfsPortOnV6() {
        assertThat(CONTAINER_V6.getDefaultInternalBucketfsPort(), equalTo(6583));
    }

    @Test
    void testDatabasePortOnV6() {
        assertThat(CONTAINER_V6.getDefaultInternalDatabasePort(), equalTo(8888));
    }

    @Test
    void testBucketfsPortOnV7() {
        assertThat(CONTAINER_V7.getDefaultInternalBucketfsPort(), equalTo(2580));
    }

    @Test
    void testDatabasePortOnV7() {
        assertThat(CONTAINER_V7.getDefaultInternalDatabasePort(), equalTo(8563));
    }
}
