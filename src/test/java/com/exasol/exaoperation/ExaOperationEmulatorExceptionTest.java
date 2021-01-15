package com.exasol.exaoperation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class ExaOperationEmulatorExceptionTest {
    @Test
    void testCreateWithOutCause() {
        final String message = "message";
        final Throwable exception = new ExaOperationEmulatorException(message);
        assertThat(exception.getMessage(), equalTo(message));
    }

    @Test
    void testCreateWithCause() {
        final IllegalArgumentException cause = new IllegalArgumentException("message of the cause");
        final Throwable exception = new ExaOperationEmulatorException("another message", cause);
        assertThat(exception.getCause(), equalTo(cause));
    }
}