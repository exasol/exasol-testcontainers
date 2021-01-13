package com.exasol.containers.workarounds;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkaroundManagerTest {
    // [utest->dsn~workaround-manager-checks-criteria~1]
    // [utest->dsn~workaround-manager-applies-multiple-of-workarounds~1]
    @Test
    void testApplyNecessaryWorkaround(@Mock final Workaround workaroundMockA, @Mock final Workaround workaroundMockB)
            throws WorkaroundException {
        when(workaroundMockA.isNecessary()).thenReturn(true);
        when(workaroundMockA.getName()).thenReturn("necessary workaround");
        when(workaroundMockB.isNecessary()).thenReturn(true);
        when(workaroundMockB.getName()).thenReturn("another necessary workaround");
        final WorkaroundManager manager = WorkaroundManager.create(Collections.emptySet(), workaroundMockA,
                workaroundMockB);
        manager.applyWorkarounds();
        verify(workaroundMockA).apply();
        verify(workaroundMockB).apply();
    }

    // [utest->dsn~workaround-manager-checks-criteria~1]
    @Test
    void testApplyUnnecessaryWorkaround(@Mock final Workaround workaroundMock) throws WorkaroundException {
        when(workaroundMock.isNecessary()).thenReturn(false);
        final WorkaroundManager manager = WorkaroundManager.create(Collections.emptySet(), workaroundMock);
        manager.applyWorkarounds();
        verify(workaroundMock, never()).apply();
    }

    @Test
    void testDontReApplyPreviousWorkaround(@Mock final Workaround workaroundMock) throws WorkaroundException {
        when(workaroundMock.getName()).thenReturn("previously applied workaround");
        final WorkaroundManager manager = WorkaroundManager.create(Set.of(workaroundMock.getName()), workaroundMock);
        manager.applyWorkarounds();
        verify(workaroundMock, never()).apply();
    }

    @Test
    void testHandleExceptionDuringWorkaroundApplication(@Mock final Workaround workaroundMock)
            throws WorkaroundException {
        when(workaroundMock.isNecessary()).thenReturn(true);
        final IllegalStateException expectedCause = new IllegalStateException("dummy cause");
        Mockito.doThrow(new WorkaroundException("Dummy exception.", expectedCause)).when(workaroundMock).apply();
        final WorkaroundManager manager = WorkaroundManager.create(Collections.emptySet(), workaroundMock);
        assertThrows(WorkaroundException.class, () -> manager.applyWorkarounds());
    }
}