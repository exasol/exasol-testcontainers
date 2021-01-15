package com.exasol.containers;

import static com.exasol.containers.ExasolService.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("slow")
class OptionalServicesIT {
    // [itest->dsn~defining-required-optional-service~1]
    @Test
    void testAllOptionalServiceRequiredByDefault() {
        try (final ExasolContainer<? extends ExasolContainer<?>> container = createContainer()) {
            container.start();
            assertAll(() -> assertTrue(container.isServiceReady(BUCKETFS)),
                    () -> assertTrue(container.isServiceReady(UDF)));
        }
    }

    private ExasolContainer<? extends ExasolContainer<?>> createContainer() {
        return new ExasolContainer<>();
    }

    // [itest->dsn~defining-required-optional-service~1]
    @Test
    void testOnlyBucketFSRequired() {
        try (final ExasolContainer<? extends ExasolContainer<?>> container = createContainer()) {
            container.withRequiredServices(BUCKETFS).start();
            assertAll(() -> assertTrue(container.isServiceReady(BUCKETFS)),
                    () -> assertFalse(container.isServiceReady(UDF)));
        }
    }

    // [itest->dsn~defining-required-optional-service~1]
    @Test
    void testOnlyUdfRequired() {
        try (final ExasolContainer<? extends ExasolContainer<?>> container = createContainer()) {
            container.withRequiredServices(UDF).start();
            assertAll(() -> assertFalse(container.isServiceReady(BUCKETFS)),
                    () -> assertTrue(container.isServiceReady(UDF)));
        }
    }

    // [itest->dsn~defining-required-optional-service~1]
    // While explicitly requesting the mandatory service "JDBC" has no particular benefit, it should still work.
    @Test
    void testRequiringJdbcService() {
        try (final ExasolContainer<? extends ExasolContainer<?>> container = createContainer()) {
            assertDoesNotThrow(() -> container.withRequiredServices(JDBC).start());
        }
    }
}