package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.io.output.ByteArrayOutputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

@ExtendWith(MockitoExtension.class)
class RemoteFileWriterTest {

    @Mock
    Ssh ssh;

    @TempDir
    Path tempDir;

    @Test
    void success() throws Exception {
        final Path local = this.tempDir.resolve("local-file.txt");
        assertThat(simulate(local, 0), equalTo(FileVisitor.fileHeader(local) + "sample file content" + '\0'));
    }

    @ParameterizedTest
    @CsvSource(value = { "3,0,0,first", "0,3,0,second", "0,0,3,final" })
    void ackFailed(final int a, final int b, final int c, final String expected) throws Exception {
        final Path local = this.tempDir.resolve("local-file.txt");
        final SshException exception = assertThrows(SshException.class, () -> simulate(local, a, b, c));
        assertThat(exception.getMessage(), equalTo(expected + " ack != 0"));
    }

    private String simulate(final Path local, final Integer first, final Integer... rest)
            throws IOException, JSchException {
        final InputStream in = mock(InputStream.class);
        when(in.read()).thenReturn(first, rest);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        final ChannelExec channel = mock(ChannelExec.class);
        when(this.ssh.openChannel(anyString())).thenReturn(channel);
        when(channel.getInputStream()).thenReturn(in);
        when(channel.getOutputStream()).thenReturn(out);

        Files.writeString(local, "sample file content");
        new RemoteFileWriter(this.ssh).write(local, "remote/path");
        return out.toString(StandardCharsets.UTF_8);
    }
}
