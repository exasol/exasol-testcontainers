package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class ExasolContainerInitializationExceptionTest {
    @Test
    void testCreate() throws Exception {
        final String message = "foo";
        final Exception cause = new InterruptedException();
        final ExasolContainerInitializationException exception = new ExasolContainerInitializationException(message,
                cause);
        assertAll(() -> assertThat(exception.getMessage(), equalTo(message)),
                () -> assertThat(exception.getCause(), equalTo(cause)));
    }
}