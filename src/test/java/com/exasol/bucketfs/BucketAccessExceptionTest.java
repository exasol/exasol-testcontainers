package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URI;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class BucketAccessExceptionTest {
    private static final URI EXPECTED_URI = URI.create("http://localhost:2580/default");
    private static final String EXPECTED_MESSAGE = "the message";
    private static final Throwable EXPECTED_CAUSE = new IllegalArgumentException();

    @Test
    void testCreateFromOtherException() throws Exception {
        final BucketAccessException exception = new BucketAccessException(EXPECTED_MESSAGE, EXPECTED_URI,
                EXPECTED_CAUSE);
        assertAll(() -> assertThat(exception.getUri(), equalTo(EXPECTED_URI)),
                () -> assertThat(exception.getMessage(), startsWith(EXPECTED_MESSAGE)),
                () -> assertThat(exception.getCause(), equalTo(EXPECTED_CAUSE)));
    }

    @Test
    void testCreateWithStatusCode() throws Exception {
        final int statusCode = 404;
        final BucketAccessException exception = new BucketAccessException(EXPECTED_MESSAGE, statusCode, EXPECTED_URI);
        assertAll(() -> assertThat(exception.getMessage(), startsWith(EXPECTED_MESSAGE)),
                () -> assertThat(exception.getUri(), equalTo(EXPECTED_URI)),
                () -> assertThat(exception.getStatusCode(), equalTo(statusCode)));
    }

    @Test
    void testCreatePlain() throws Exception {
        final BucketAccessException exception = new BucketAccessException(EXPECTED_MESSAGE, EXPECTED_CAUSE);
        assertAll(() -> assertThat(exception.getMessage(), startsWith(EXPECTED_MESSAGE)),
                () -> assertThat(exception.getCause(), equalTo(EXPECTED_CAUSE)));
    }
}