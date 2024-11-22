package com.exasol.containers;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@Testcontainers
class ExposedPortsIT {

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>()
            .withExposedPorts(8563)
            .withReuse(true);

    @Test
    void testExposedPorts() {
        assertThat(EXASOL.getExposedPorts(), containsInAnyOrder(8563));
    }
}