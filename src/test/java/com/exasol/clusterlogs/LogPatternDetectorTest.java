package com.exasol.clusterlogs;

import static com.exasol.testutil.VarArgsMatcher.anyStrings;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.ExecResultFactory;

import com.exasol.containers.ExasolContainer;
import com.exasol.testutil.ExceptionAssertions;

@ExtendWith(MockitoExtension.class)
class LogPatternDetectorTest {
    private static final String LOG_PATH = "path";
    private static final String LOG_NAME_PATTERN = "logfilename";
    private static final String PATTERN = "pattern";
    private static final String STD_OUT_RESULT = "actual log content";
    @Mock
    private ExasolContainer<? extends ExasolContainer<?>> containerMock;
    @Mock
    private LogEntryPatternVerifier logEntryVerifier;
    private LogPatternDetector detector;

    @BeforeEach
    void setup() {
        this.detector = LogPatternDetector.builder() //
                .container(this.containerMock) //
                .logPath(LOG_PATH) //
                .logNamePattern(LOG_NAME_PATTERN) //
                .pattern(PATTERN) //
                .logEntryVerifier(this.logEntryVerifier) //
                .build();
    }

    @Test
    void testGetActualLogSucceeds() throws UnsupportedOperationException, IOException, InterruptedException {
        when(this.containerMock.execInContainer(anyStrings()))
                .thenReturn(ExecResultFactory.result(0, STD_OUT_RESULT, "stderr"));

        assertThat(this.detector.getActualLog(), equalTo(STD_OUT_RESULT));
    }

    @Test
    void testGetActualLogFailsWithInterruptedException()
            throws UnsupportedOperationException, IOException, InterruptedException {
        when(this.containerMock.execInContainer(anyStrings())).thenThrow(new InterruptedException("expected"));
        ExceptionAssertions.assertThrowsWithMessage(IllegalStateException.class, () -> this.detector.getActualLog(),
                "InterruptedException when reading log file content");
    }

    @Test
    void testGetActualLogFailsWithIOException()
            throws UnsupportedOperationException, IOException, InterruptedException {
        when(this.containerMock.execInContainer(anyStrings())).thenThrow(new IOException("expected"));

        ExceptionAssertions.assertThrowsWithMessage(UncheckedIOException.class, () -> this.detector.getActualLog(),
                "F-ETC-6: Exception reading content of file(s) '" + LOG_PATH + "'/'" + LOG_NAME_PATTERN + "'");
    }

    @Test
    void describe() {
        assertThat(this.detector.describe(), equalTo(
                "Scanning for log message pattern \"pattern\" in \"path/logfilename\", using logEntryVerifier."));
    }
}
