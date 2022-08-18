package com.exasol.clusterlogs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;
import com.exasol.bucketfs.monitor.LineCountState;
import com.exasol.containers.ExasolContainer;

@ExtendWith(MockitoExtension.class)
class LogPatternDetectorFactoryTest {

    @Mock
    private ExasolContainer<? extends ExasolContainer<?>> containerMock;
    private final LogPatternDetectorFactory factory = new LogPatternDetectorFactory(this.containerMock);

    @Test
    void unsupportedState() {
        final State state = Mockito.mock(State.class);
        assertThrows(IllegalArgumentException.class,
                () -> this.factory.createLogPatternDetector("logpath", "logname", "pattern", state));
    }

    @Test
    void lineNumberBasedState() {
        final LineCountState state = Mockito.mock(LineCountState.class);
        final LogPatternDetector detector = this.factory.createLogPatternDetector("logpath", "logname", "pattern",
                state);
        assertThat(detector, notNullValue());
    }
}
