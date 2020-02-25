package com.exasol.exaoperation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.Container;

import com.exasol.exaoperation.plugin.PluginStub;

@ExtendWith(MockitoExtension.class)
class ExaOperationEmulatorTest {
    @Mock
    private Container<? extends Container<?>> containerMock;

    @Test
    void testInstallPluginPackageCatchesUnsupportedOperationException() throws Exception {
        when(this.containerMock.execInContainer(eq("tar"), eq("xf"), any(), eq("-C"), any()))
                .thenThrow(new UnsupportedOperationException());
        assertWrappedException(this.containerMock, "Unable to install plug-in");
    }

    private void assertWrappedException(final Container<? extends Container<?>> containerMock,
            final String expectedMessagePrefix) {
        final ExaOperation exaOperation = new ExaOperationEmulator(containerMock);
        final ExaOperationEmulatorException exception = assertThrows(ExaOperationEmulatorException.class,
                () -> exaOperation.installPluginPackage(PluginStub.PLUGIN_PACKAGE_PATH));
        assertThat(exception.getMessage(), startsWith(expectedMessagePrefix));
    }

    @Test
    void testInstallOuterPackageCatchesIOException() throws Exception {
        when(this.containerMock.execInContainer(eq("tar"), eq("xf"), any(), eq("-C"), any()))
                .thenThrow(new IOException());
        assertWrappedException(this.containerMock, "Unable to install plug-in");
    }
}