package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.ExecResultFactory;

import com.exasol.testutil.ExceptionAssertions;

@ExtendWith(MockitoExtension.class)
class ContainerFileOperationsTest {

    private static final String PATH = "path";
    private static final Charset CHARSET = StandardCharsets.US_ASCII;
    private static final String FILE_CONTENT = "file content";

    @Mock
    private ExasolContainer<? extends ExasolContainer<?>> containerMock;
    private ContainerFileOperations fileOperations;

    @BeforeEach
    void setUp() {
        this.fileOperations = new ContainerFileOperations(this.containerMock);
    }

    @Test
    void testReadFileSucceeds()
            throws ExasolContainerException, UnsupportedOperationException, IOException, InterruptedException {
        when(this.containerMock.execInContainer(CHARSET, "cat", PATH))
                .thenReturn(ExecResultFactory.result(0, FILE_CONTENT, ""));

        assertThat(this.fileOperations.readFile(PATH, CHARSET), equalTo(FILE_CONTENT));
    }

    @Test
    void testReadFileFailsWithStdErrorContent()
            throws ExasolContainerException, UnsupportedOperationException, IOException, InterruptedException {
        when(this.containerMock.execInContainer(CHARSET, "cat", PATH))
                .thenReturn(ExecResultFactory.result(0, FILE_CONTENT, "stderr message"));

        ExceptionAssertions.assertThrowsWithMessage(ExasolContainerException.class,
                () -> this.fileOperations.readFile(PATH, CHARSET),
                "F-ETC-10: Unable to read file 'path' from container. Error message: 'stderr message'.");
    }

    @Test
    void testReadFileFailsWithIoException()
            throws ExasolContainerException, UnsupportedOperationException, IOException, InterruptedException {
        when(this.containerMock.execInContainer(CHARSET, "cat", PATH)).thenThrow(new IOException("expected"));

        ExceptionAssertions.assertThrowsWithMessage(UncheckedIOException.class,
                () -> this.fileOperations.readFile(PATH, CHARSET),
                "F-ETC-11: Unable to read file 'path' from container.");
    }

    @Test
    void testReadFileFailsWithInterruptedException()
            throws ExasolContainerException, UnsupportedOperationException, IOException, InterruptedException {
        when(this.containerMock.execInContainer(CHARSET, "cat", PATH)).thenThrow(new InterruptedException("expected"));

        ExceptionAssertions.assertThrowsWithMessage(IllegalStateException.class,
                () -> this.fileOperations.readFile(PATH, CHARSET),
                "F-ETC-12: InterruptedException when reading file content");
    }
}
