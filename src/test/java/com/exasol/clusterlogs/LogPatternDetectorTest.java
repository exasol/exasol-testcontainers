package com.exasol.clusterlogs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.ExecResultFactory;

@ExtendWith(MockitoExtension.class)
class LogPatternDetectorTest {
    private static final String LOG_PATH = "path";
    private static final String LOG_NAME_PATTERN = "logfilename";
    private static final String PATTERN = "pattern";
    private static final String STD_OUT_RESULT = "actual log content";
    @Mock
    private Container<? extends Container<?>> containerMock;
    @Mock
    private LogEntryPatternVerifier logEntryVerifier;
    private LogPatternDetector detector;

    @BeforeEach
    void setup() {
        this.detector = new LogPatternDetector(this.containerMock, LOG_PATH, LOG_NAME_PATTERN, PATTERN,
                this.logEntryVerifier);
    }

    @Test
    void testGetActualLogSucceeds() throws UnsupportedOperationException, IOException, InterruptedException {
        when(this.containerMock.execInContainer(any()))
                .thenReturn(ExecResultFactory.mockResult(0, STD_OUT_RESULT, "stderr"));

        assertThat(this.detector.getActualLog(), equalTo(STD_OUT_RESULT));
    }

    @Test
    void testGetActualLogFailsWithInterruptedException()
            throws UnsupportedOperationException, IOException, InterruptedException {
        when(this.containerMock.execInContainer(any())).thenThrow(new InterruptedException("expected"));

        assertThrows(IllegalStateException.class, () -> this.detector.getActualLog());
    }

    @Test
    void testGetActualLogFailsWithIOException()
            throws UnsupportedOperationException, IOException, InterruptedException {
        when(this.containerMock.execInContainer(any())).thenThrow(new IOException("expected"));

        final UncheckedIOException exception = assertThrows(UncheckedIOException.class,
                () -> this.detector.getActualLog());
        assertThat(exception.getMessage(),
                equalTo("F-ETC-6: Exception reading content of file(s) '" + LOG_PATH + "'/'" + LOG_NAME_PATTERN + "'"));
    }

    @Test
    void describe() {
        assertThat(this.detector.describe(),
                equalTo("Scanning for log message pattern \"pattern in \"path/logfilename\". using logEntryVerifier"));
    }
}
