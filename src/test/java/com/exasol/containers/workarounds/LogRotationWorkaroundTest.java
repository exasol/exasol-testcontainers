package com.exasol.containers.workarounds;

import static com.exasol.testutil.VarArgsMatcher.anyStrings;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.ExecResultFactory;

import com.exasol.containers.DockerImageReferenceFactory;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.exec.ExitCode;

@Tag("fast")
@ExtendWith(MockitoExtension.class)
class LogRotationWorkaroundTest {
    @Test
    void testGetName(@Mock final ExasolContainer<? extends ExasolContainer<?>> exasolMock) {
        assertThat(new LogRotationWorkaround(exasolMock).getName(), equalTo("log rotation"));
    }

    // [utest->dsn~log-rotation-workaround-criteria~1]
    @CsvSource({ //
            "6.2, false, true", //
            "7.0, false, true", //
            "7.1, false, false", //
            "8  , false, false", //
            "6.2, true , false", //
            "7.0, true , false", //
            "7.1, true , false", //
            "8  , true , false", //
    })
    @ParameterizedTest
    void testNecessaryWhenReused(final String reference, final boolean reused, final boolean necessary,
            @Mock final ExasolContainer<? extends ExasolContainer<?>> exasolMock) {
        if (reused) {
            when(exasolMock.isReused()).thenReturn(reused);
        } else {
            when(exasolMock.getDockerImageReference()).thenReturn(DockerImageReferenceFactory.parse(reference));
        }
        final Workaround workaround = new LogRotationWorkaround(exasolMock);
        assertThat(workaround.isNecessary(), equalTo(necessary));
    }

    // [utest->dsn~log-rotation-workaround~1]
    @Test
    void testApply(@Mock final ExasolContainer<? extends ExasolContainer<?>> exasolMock)
            throws WorkaroundException, UnsupportedOperationException, IOException, InterruptedException {
        final ExecResult mockResult = ExecResultFactory.result(ExitCode.OK, "", "");
        when(exasolMock.execInContainer(anyStrings())).thenReturn(mockResult);
        final Workaround workaround = new LogRotationWorkaround(exasolMock);
        workaround.apply();
        verify(exasolMock).execInContainer("sed", "-i", "-es/'bucketfsd[^']*log' //", "/etc/cron.daily/exa-logrotate");
    }

    @Test
    void testApplyHandlesUnsupportedOperationException(
            @Mock final ExasolContainer<? extends ExasolContainer<?>> exasolMock)
            throws UnsupportedOperationException, IOException, InterruptedException {
        assertExceptionWrapped(exasolMock, new UnsupportedOperationException("not supported"));
    }

    private void assertExceptionWrapped(final ExasolContainer<? extends ExasolContainer<?>> exasolMock,
            final Throwable cause) throws IOException, InterruptedException {
        when(exasolMock.execInContainer(anyStrings())).thenThrow(cause);
        final Workaround workaround = new LogRotationWorkaround(exasolMock);
        final WorkaroundException exception = assertThrows(WorkaroundException.class, () -> workaround.apply());
        assertThat(exception.getCause(), equalTo(cause));
    }

    @Test
    void testApplyHandlesIOException(@Mock final ExasolContainer<? extends ExasolContainer<?>> exasolMock)
            throws UnsupportedOperationException, IOException, InterruptedException {
        assertExceptionWrapped(exasolMock, new IOException("access denied"));
    }

    @Test
    void testApplyHandlesInterruption(@Mock final ExasolContainer<? extends ExasolContainer<?>> exasolMock)
            throws UnsupportedOperationException, IOException, InterruptedException {
        when(exasolMock.execInContainer(anyStrings())).thenThrow(new InterruptedException("stop"));
        final Workaround workaround = new LogRotationWorkaround(exasolMock);
        final WorkaroundException exception = assertThrows(WorkaroundException.class, () -> workaround.apply());
        assertThat(exception.getMessage(), containsString("Interrupted"));
    }

    @Test
    void testApplyHandlesNegativeExitCode(@Mock final ExasolContainer<? extends ExasolContainer<?>> exasolMock)
            throws UnsupportedOperationException, IOException, InterruptedException {
        final ExecResult mockResult = ExecResultFactory.result(-1, "", "reason");
        when(exasolMock.execInContainer(anyStrings())).thenReturn(mockResult);
        final Workaround workaround = new LogRotationWorkaround(exasolMock);
        final WorkaroundException exception = assertThrows(WorkaroundException.class, () -> workaround.apply());
        assertThat(exception.getMessage(), containsString("Error during command execution: reason"));
    }
}