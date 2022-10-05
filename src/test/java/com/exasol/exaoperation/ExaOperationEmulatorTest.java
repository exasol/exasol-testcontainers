package com.exasol.exaoperation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.ExecResultFactory;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.exec.ExitCode;
import com.exasol.exaoperation.plugin.PluginStub;

@Tag("fast")
@ExtendWith(MockitoExtension.class)
class ExaOperationEmulatorTest {
    @Mock
    private ExasolContainer<? extends ExasolContainer<?>> containerMock;

    @Test
    void testInstallPluginPackageCatchesUnsupportedOperationException() throws Exception {
        doThrow(new UnsupportedOperationException()).when(this.containerMock).execInContainer(eq("tar"), eq("xf"),
                any(), eq("-C"), any());
        doReturn(ExecResultFactory.result(0, "/tmp/tmp.asdf", "")).when(this.containerMock)
                .execInContainer("/bin/mktemp", "--directory", "--tmpdir=/tmp", "tmp.XXXXXXXX-plugin");
        assertWrappedException(this.containerMock, "Unable to install plug-in");
    }

    private void assertWrappedException(final ExasolContainer<? extends ExasolContainer<?>> containerMock,
            final String expectedMessagePrefix) {
        final ExaOperation exaOperation = new ExaOperationEmulator(containerMock);
        final ExaOperationEmulatorException exception = assertThrows(ExaOperationEmulatorException.class,
                () -> exaOperation.installPluginPackage(PluginStub.PLUGIN_PACKAGE_PATH));
        assertThat(exception.getMessage(), startsWith(expectedMessagePrefix));
    }

    @Test
    void testInstallOuterPackageCatchesIOException() throws Exception {
        doThrow(new IOException()).when(this.containerMock).execInContainer(eq("tar"), eq("xf"), any(), eq("-C"),
                any());
        doReturn(ExecResultFactory.result(ExitCode.OK, "/tmp/tmp.asdf", "")).when(this.containerMock)
                .execInContainer("/bin/mktemp", "--directory", "--tmpdir=/tmp", "tmp.XXXXXXXX-plugin");
        assertWrappedException(this.containerMock, "Unable to install plug-in");
    }
}