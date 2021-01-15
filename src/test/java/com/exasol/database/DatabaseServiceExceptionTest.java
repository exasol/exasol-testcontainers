package com.exasol.database;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class DatabaseServiceExceptionTest {
    @Test
    void testGetDatabaseName() {
        assertThat(new DatabaseServiceException("foo", "a message").getDatabaseName(), equalTo("foo"));
    }

    @Test
    void testCreateWithCause() {
        final IllegalArgumentException cause = new IllegalArgumentException("message of the cause");
        final Throwable exception = new DatabaseServiceException("db1", "another message", cause);
        assertThat(exception.getCause(), equalTo(cause));
    }
}