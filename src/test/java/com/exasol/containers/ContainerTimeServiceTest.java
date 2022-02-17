package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

@ExtendWith(MockitoExtension.class)
class ContainerTimeServiceTest {
    @Mock
    Container<? extends Container<?>> containerMock;

    @Test
    void testGetMillisSinceEpochThrowsExceptionWhenDateReturnsError() throws UnsupportedOperationException {
        final ExecResult resultStub = createExecResultStub(1, null, null);
        whenExecDate().thenReturn(resultStub);
        final ContainerTimeService service = ContainerTimeService.create(this.containerMock);
        final ExasolContainerException exception = assertThrows(ExasolContainerException.class,
                () -> service.getMillisSinceEpochUtc());
        assertThat(exception.getMessage(), containsString("Unable to get ISO time from container via 'date' command"));
    }

    private OngoingStubbing<ExecResult> whenExecDate() {
        try {
            return when(this.containerMock.execInContainer("date", "+%s%3N"));
        } catch (final Throwable excpetion) {
            throw new AssertionError("Unable to mock 'execInContainer'", excpetion);
        }
    }

    // The ExecResult class has a hidden constructor, so we need reflection to stub the class.
    private ExecResult createExecResultStub(final int exitCode, final String stdout, final String stderr) {
        try {
            final Class<?> resultClass = Class.forName("org.testcontainers.containers.Container$ExecResult");
            final Constructor<?> constructor = resultClass.getDeclaredConstructor(int.class, String.class,
                    String.class);
            constructor.setAccessible(true);
            return (ExecResult) constructor.newInstance(exitCode, stdout, stderr);
        } catch (final Throwable exception) {
            throw new AssertionError("Unable to stub ExecResult.", exception);
        }
    }

    private static Stream<Arguments> getExceptionVariants() {
        return Stream.of( //
                Arguments.of(new IOException("Dummy IOExecption"), "Unable to get current time"), //
                Arguments.of(new UnsupportedOperationException("Dummy UOException"), "Unable to get current time"), //
                Arguments.of(new InterruptedException("Dummy Interruption"), "") //
        );
    }

    @MethodSource("getExceptionVariants")
    @ParameterizedTest
    void testGetMillisSinceEpochWrapsIOExceptions(final Exception incomingException,
            final String expectedMessageFragment) {
        whenExecDate().thenThrow(incomingException);
        final ContainerTimeService service = ContainerTimeService.create(this.containerMock);
        final ExasolContainerException exception = assertThrows(ExasolContainerException.class,
                () -> service.getMillisSinceEpochUtc());
        assertThat(exception.getMessage(), containsString(expectedMessageFragment));
    }
}