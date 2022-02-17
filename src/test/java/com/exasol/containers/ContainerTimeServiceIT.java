package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("slow")
@Testcontainers
class ContainerTimeServiceIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withReuse(true);
    private static ContainerTimeService service;

    @BeforeAll
    static void beforeAll() {
        service = ContainerTimeService.create(CONTAINER);
    }

    // Since the Java abstraction for instants and durations are expensive, we accept a bigger offset in this case (one
    // second).
    @Test
    void testGetTime() {
        final Instant containerTime = service.getTime();
        final Duration offset = Duration.between(Instant.now(), containerTime);
        final Duration maxOffset = Duration.ofSeconds(1);
        assertThat(offset, lessThanOrEqualTo(maxOffset));
    }

    @Test
    void testGetMillisSinceEpochUtcCloseEnoughToHostClock() {
        final BigDecimal containerTime = BigDecimal.valueOf(service.getMillisSinceEpochUtc());
        final BigDecimal hostTime = BigDecimal.valueOf(System.currentTimeMillis());
        final BigDecimal acceptableError = BigDecimal.valueOf(MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS);
        assertThat(containerTime, closeTo(hostTime, acceptableError));
    }
}