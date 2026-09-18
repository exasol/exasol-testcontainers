package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.jcraft.jsch.JSchException;

// [utest->dsn~reverse-ssh-port-forwarding-to-test-host~1]
class SshPortForwarderTest {
    @Test
    void addsReversePortForwarding() throws JSchException {
        final Ssh ssh = mock(Ssh.class);
        when(ssh.addReversePortForwarding("*", 1234)).thenReturn(4321);
        try (final SshPortForwarder forwarder = new SshPortForwarder(ssh)) {
            assertThat(forwarder.addReversePortForwarding(1234), equalTo(4321));
            verify(ssh).addReversePortForwarding("*", 1234);
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
    void reportsFailedReversePortForwarding() throws JSchException {
        final Ssh ssh = mock(Ssh.class);
        when(ssh.addReversePortForwarding("*", 1234)).thenThrow(new JSchException("failure"));
        try (final SshPortForwarder forwarder = new SshPortForwarder(ssh)) {
            final SshException exception = assertThrows(SshException.class, () -> forwarder.addReversePortForwarding(1234));
            assertThat(exception.getMessage(), equalTo(
                    "E-ETC-50: Failed to set up reverse SSH port forwarding for local port 1234 This is an internal error that should not happen. Please report it by opening a GitHub issue."));
        }
    }
}
