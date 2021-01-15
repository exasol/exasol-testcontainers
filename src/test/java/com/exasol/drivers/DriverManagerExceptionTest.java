package com.exasol.drivers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("fast")
@ExtendWith(MockitoExtension.class)
class DriverManagerExceptionTest {
    @Test
    void testCreateWithDriver(@Mock final DatabaseDriver driverMock) {
        final String expectedMessage = "the message";
        final Throwable expectedCause = new IllegalArgumentException("dummy exception");
        final String expectedDriverMessage = "the driver";
        when(driverMock.toString()).thenReturn(expectedDriverMessage);
        final DriverManagerException exception = new DriverManagerException(expectedMessage, driverMock, expectedCause);
        assertAll(() -> assertThat(exception.getMessage(), equalTo(expectedMessage + " " + expectedDriverMessage)), //
                () -> assertThat(exception.getDriver(), equalTo(driverMock)), //
                () -> assertThat(exception.getCause(), equalTo(expectedCause)), //
                () -> assertThat(exception.hasDriver(), equalTo(true)));
    }

    @Test
    void testCreateWithOutDriver() {
        final String expectedMessage = "the message";
        final Throwable expectedCause = new IllegalArgumentException("dummy exception");
        final DriverManagerException exception = new DriverManagerException(expectedMessage, expectedCause);
        assertAll(() -> assertThat(exception.getMessage(), equalTo(expectedMessage)), //
                () -> assertThat(exception.getDriver(), nullValue()), //
                () -> assertThat(exception.getCause(), equalTo(expectedCause)), //
                () -> assertThat(exception.hasDriver(), equalTo(false)));
    }
}