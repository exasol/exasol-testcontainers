package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.containers.ssh.FileVisitor.ContentProcessor;
import com.jcraft.jsch.*;

@ExtendWith(MockitoExtension.class)
class FileVisitorTest {

    @Mock
    Ssh ssh;
    @Mock
    ContentProcessor processor;

    @ParameterizedTest
    @ValueSource(ints = { 1, 2 })
    void channelReturnsErrorCode(final int ack1) throws IOException, JSchException {
        final Channel channel = mockChannel(ack1, "(simulated error)\n");
        final FileVisitor testee = new FileVisitor(this.ssh);
        assertThrows(SshException.class, () -> testee.visit("/remote/file", this.processor));
        verify(channel).connect();
        verify(channel).disconnect();
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1, 3 })
    void channelWithoutError(final int ack1) throws IOException, JSchException {
        final Channel channel = mockChannel(ack1, "(simulated error)\n");
        final FileVisitor testee = new FileVisitor(this.ssh);
        testee.visit("/remote/file", this.processor);
        verify(channel).connect();
        verify(channel).disconnect();
    }

    @Test
    void success() throws IOException, JSchException {
        final Channel channel = mockValidChannel(0);
        final FileVisitor testee = new FileVisitor(this.ssh);

        when(this.processor.process(any(), any(), anyInt())).thenReturn("result");

        final String result = testee.visit("/remote/file", this.processor);
        assertThat(result, equalTo("result"));
        verify(channel).connect();
        verify(channel).disconnect();
    }

    @Test
    void exception() throws IOException, JSchException {
        mockValidChannel(3);
        final FileVisitor testee = new FileVisitor(this.ssh);
        when(this.processor.process(any(), any(), anyInt())).thenReturn("result");
        assertThrows(SshException.class, () -> testee.visit("/remote/file", this.processor));
    }

    private Channel mockValidChannel(final int ack2) throws IOException, JSchException {
        final String mode = "0644";
        final long filesize = 1023L;
        final String remotePath = "/remote/file";
        return mockChannel(FileVisitor.fileHeader('C', mode, filesize, remotePath) + (char) ack2);
    }

    private Channel mockChannel(final int ack1, final String rest) throws IOException, JSchException {
        return mockChannel("" + (char) ack1 + rest);
    }

    private Channel mockChannel(final String input) throws IOException, JSchException {
        final ChannelExec channel = mock(ChannelExec.class);
        when(this.ssh.openChannel(anyString())).thenReturn(channel);
        final OutputStream out = mock(OutputStream.class);

        when(channel.getInputStream()).thenReturn(inputStream(input));
        when(channel.getOutputStream()).thenReturn(out);
        return channel;
    }

    private InputStream inputStream(final String content) {
        return new ByteArrayInputStream(content.getBytes());
    }
}
