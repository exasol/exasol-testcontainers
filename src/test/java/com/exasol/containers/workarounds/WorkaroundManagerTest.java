package com.exasol.containers.workarounds;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("fast")
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

    @Test
    void testReportAppliedWorkarounds(@Mock final Workaround workaroundMockA, @Mock final Workaround workaroundMockB,
            @Mock final Workaround workaroundMockC, @Mock final Workaround workaroundMockD) throws WorkaroundException {
        when(workaroundMockA.isNecessary()).thenReturn(true);
        when(workaroundMockA.getName()).thenReturn("A");
        when(workaroundMockB.isNecessary()).thenReturn(true);
        when(workaroundMockB.getName()).thenReturn("B");
        when(workaroundMockC.isNecessary()).thenReturn(false);
        when(workaroundMockC.getName()).thenReturn("C");
        when(workaroundMockD.getName()).thenReturn("D");
        final WorkaroundManager manager = WorkaroundManager.create(Set.of("D"), workaroundMockA, workaroundMockB,
                workaroundMockC, workaroundMockD);
        final Set<Workaround> appliedWorkarounds = manager.applyWorkarounds();
        assertThat(appliedWorkarounds, containsInAnyOrder(workaroundMockA, workaroundMockB));
    }
}