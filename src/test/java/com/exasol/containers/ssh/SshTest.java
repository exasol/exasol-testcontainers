package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.exasol.containers.ssh.RemoteFileParser.LineMatcher;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

class SshTest {

    @Test
    void create() throws JSchException, IOException {
        final Session session = mock(Session.class);
        final Ssh ssh = new Ssh(session);
        assertThat(ssh.createRemoteExecutor(), isA(RemoteExecutor.class));
        assertThat(ssh.createRemoteFileReader(), isA(RemoteFileReader.class));
        assertThat(ssh.createRemoteFileParser(), isA(RemoteFileParser.class));
    }

    @Test
    void charset() throws JSchException, IOException {
        final Session session = mock(Session.class);
        final Charset charset = StandardCharsets.ISO_8859_1;
        final Ssh ssh = new Ssh(session).withCharset(charset);
        assertThat(ssh.getCharset(), equalTo(charset));
    }

    @Test
    void openChannel() throws JSchException, IOException {
        final Session session = mock(Session.class);
        when(session.isConnected()).thenReturn(false, true);
        final Ssh ssh = new Ssh(session);
        ssh.openChannel("type");
        ssh.openChannel("type");
        verify(session, times(1)).connect();
    }

    @Test
    void disconnect() throws JSchException, IOException {
        final Session session = mock(Session.class);
        final Ssh ssh = new Ssh(session);
        ssh.disconnect();
        verify(session).disconnect();
    }

    @Test
    void execute() throws JSchException, IOException {
        final Ssh ssh = mock(Ssh.class);
        final RemoteExecutor mock = mock(RemoteExecutor.class);
        when(ssh.createRemoteExecutor()).thenReturn(mock);
        when(ssh.execute(anyString())).thenCallRealMethod();
        ssh.execute("command");
        verify(mock).execute("command");
    }

    @Test
    void readRemoteFile() throws JSchException, IOException {
        final Ssh ssh = mock(Ssh.class);
        final RemoteFileReader mock = mock(RemoteFileReader.class);
        when(ssh.createRemoteFileReader()).thenReturn(mock);
        when(ssh.readRemoteFile(anyString())).thenCallRealMethod();
        ssh.readRemoteFile("/remote/path");
        verify(mock).read("/remote/path");
    }

    @Test
    void parseRemoteFile() throws JSchException, IOException {
        final Ssh ssh = mock(Ssh.class);
        final RemoteFileParser mock = mock(RemoteFileParser.class);
        when(ssh.createRemoteFileParser()).thenReturn(mock);
        when(ssh.parseRemoteFile(anyString(), any(LineMatcher.class))).thenCallRealMethod();
        final LineMatcher matcher = mock(LineMatcher.class);
        ssh.parseRemoteFile("/remote/path", matcher);
        verify(mock).findMatch("/remote/path", matcher);
    }
}
