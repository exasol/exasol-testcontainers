package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ExecResultFactory;

import com.jcraft.jsch.JSchException;

// [utest->dsn~reverse-ssh-port-forwarding-to-test-host~1]
class SshPortForwarderTest {
    @Test
    void addsReversePortForwarding() throws IOException, JSchException {
        final Ssh ssh = mock(Ssh.class);
        when(ssh.execute(anyString())).thenReturn(ExecResultFactory.result(0, "4321\n", ""));
        try (final SshPortForwarder forwarder = new SshPortForwarder(ssh)) {
            assertThat(forwarder.addReversePortForwarding(1234), equalTo(4321));
            verify(ssh).addReversePortForwarding("*", 4321, "localhost", 1234);
        }
    }

    @Test
    void closesSshConnection() {
        final Ssh ssh = mock(Ssh.class);
        final SshPortForwarder forwarder = new SshPortForwarder(ssh);

        forwarder.close();

        verify(ssh).disconnect();
    }

    @Test
    void reportsFailedPortLookup() throws IOException {
        final Ssh ssh = mock(Ssh.class);
        when(ssh.execute(anyString())).thenReturn(ExecResultFactory.result(1, "", "failure"));
        try (final SshPortForwarder forwarder = new SshPortForwarder(ssh)) {
            final SshException exception = assertThrows(SshException.class, () -> forwarder.addReversePortForwarding(1234));
            assertThat(exception.getMessage(), startsWith("E-ETC-51: Failed to find a free TCP port in the Exasol database: 'failure'"));
        }
    }
}
