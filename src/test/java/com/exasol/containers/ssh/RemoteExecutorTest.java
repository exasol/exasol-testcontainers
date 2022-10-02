package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.containers.ssh.RemoteExecutor.Result;
import com.exasol.containers.ssh.RemoteExecutor.Sleeper;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

@ExtendWith(MockitoExtension.class)
public class RemoteExecutorTest {

    static final Charset CHARSET = StandardCharsets.UTF_8;

    @Mock
    Ssh ssh;
    @Mock
    Sleeper sleeper;
    @Mock
    ChannelExec channel;

    @Test
    void closed() throws IOException, JSchException {
        when(this.channel.isClosed()).thenReturn(true);
        verifyNoInteractions(this.sleeper);
        verifyResult();
    }

    @Test
    void openThenClosed() throws IOException, JSchException {
        when(this.channel.isClosed()).thenReturn(false, false, true);
        verifyResult();
        verify(this.sleeper, times(2)).sleep(1000);
        verifyNoMoreInteractions(this.sleeper);
    }

    private void verifyResult() throws IOException, JSchException {
        final InputStream stream = new StreamSimulator();
        when(this.channel.getInputStream()).thenReturn(stream);
        when(this.channel.getExitStatus()).thenReturn(123);

        when(this.ssh.getCharset()).thenReturn(CHARSET);
        when(this.ssh.openChannel(anyString())).thenReturn(this.channel);

        final RemoteExecutor testee = new RemoteExecutor(this.ssh, this.sleeper);
        final Result result = testee.execute("command");
        assertThat(result.getExitCode(), equalTo(123));
        assertThat(result.getStdout(), equalTo("first,second"));
    }
}
