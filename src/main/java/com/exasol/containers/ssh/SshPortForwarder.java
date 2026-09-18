package com.exasol.containers.ssh;

import com.exasol.errorreporting.ExaError;
import com.jcraft.jsch.JSchException;

/**
 * Creates reverse SSH port forwardings from an Exasol database to the local test host.
 */
final class SshPortForwarder implements AutoCloseable {
    private final Ssh ssh;

    SshPortForwarder(final SessionBuilder sessionBuilder) {
        this(new Ssh(sessionBuilder.build()));
    }

    SshPortForwarder(final Ssh ssh) {
        this.ssh = ssh;
    }

    int addReversePortForwarding(final int localPort) {
        try {
            return this.ssh.addReversePortForwarding("*", localPort);
        } catch (final JSchException exception) {
            throw new SshException(ExaError.messageBuilder("E-ETC-50")
                    .message("Failed to set up reverse SSH port forwarding for local port {{localPort}}", localPort)
                    .ticketMitigation().toString(), exception);
        }
    }

    @Override
    public void close() {
        this.ssh.disconnect();
    }
}
