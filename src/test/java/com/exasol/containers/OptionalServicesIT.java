package com.exasol.containers;

import static com.exasol.containers.ExasolService.BUCKETFS;
import static com.exasol.containers.ExasolService.UDF;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
}