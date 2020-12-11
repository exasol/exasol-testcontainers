package com.exasol.containers.workarounds;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkaroundManagerTest {
    @Test
    void testApplyNecessaryWorkaround(@Mock final Workaround workaroundMock) throws WorkaroundException {
        when(workaroundMock.isNecessary()).thenReturn(true);
        when(workaroundMock.getName()).thenReturn("necessary workaround");
        final WorkaroundManager manager = WorkaroundManager.create(workaroundMock);
        manager.applyWorkarounds();
        verify(workaroundMock).apply();
    }

    @Test
    void testApplyUnnecessaryWorkaround(@Mock final Workaround workaroundMock) throws WorkaroundException {
        when(workaroundMock.isNecessary()).thenReturn(false);
        final WorkaroundManager manager = WorkaroundManager.create(workaroundMock);
        manager.applyWorkarounds();
        verify(workaroundMock, never()).apply();
    }

    @Test
    void testHandleExceptionDuringWorkaroundApplication(@Mock final Workaround workaroundMock)
            throws WorkaroundException {
        when(workaroundMock.isNecessary()).thenReturn(true);
        final IllegalStateException expectedCause = new IllegalStateException("dummy cause");
        Mockito.doThrow(new WorkaroundException("Dummy exception.", expectedCause)).when(workaroundMock).apply();
        final WorkaroundManager manager = WorkaroundManager.create(workaroundMock);
        assertThrows(WorkaroundException.class, () -> manager.applyWorkarounds());
    }
}