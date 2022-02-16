package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("slow")
@Testcontainers
class ContainerTimeServiceIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withReuse(true);

    @Test
    void testGetContainerTime() {
        final ContainerTimeService service = ContainerTimeService.create(CONTAINER);
        final Instant containerTime = service.getTime();
        final Duration offset = Duration.between(Instant.now(), containerTime);
        final Duration maxOffset = Duration.of(ExasolContainerConstants.MAX_ALLOWED_CLOCK_OFFSET_IN_MILLIS,
                ChronoUnit.MILLIS);
        assertThat(offset, lessThanOrEqualTo(maxOffset));
    }
}