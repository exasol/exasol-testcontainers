package com.exasol.containers.ssh;

import java.io.IOException;

import org.testcontainers.containers.Container.ExecResult;

import com.exasol.errorreporting.ExaError;
import com.jcraft.jsch.JSchException;

/**
 * Creates reverse SSH port forwardings from an Exasol database to the local test host.
 */
final class SshPortForwarder implements AutoCloseable {
    private static final String LOCALHOST = "localhost";
    private static final String PORT_FINDER_COMMAND = "python <<HEREDOC\n"
            + "import socket\n"
            + "from contextlib import closing\n"
            + "with closing(socket.socket(socket.AF_INET, socket.SOCK_STREAM)) as s:\n"
            + "    s.bind(('', 0))\n"
            + "    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)\n"
            + "    print(s.getsockname()[1])\n"
            + "HEREDOC";

    private final Ssh ssh;

    SshPortForwarder(final SessionBuilder sessionBuilder) {
        this(new Ssh(sessionBuilder.build()));
    }

    SshPortForwarder(final Ssh ssh) {
        this.ssh = ssh;
    }

    int addReversePortForwarding(final int localPort) {
        final int remotePort = findFreePortOnDatabase();
        try {
            this.ssh.addReversePortForwarding("*", remotePort, LOCALHOST, localPort);
            return remotePort;
        } catch (final JSchException exception) {
            throw new SshException(ExaError.messageBuilder("E-ETC-50")
                    .message("Failed to set up reverse SSH port forwarding.")
                    .ticketMitigation().toString(), exception);
        }
    }

    private int findFreePortOnDatabase() {
        try {
            final ExecResult result = this.ssh.execute(PORT_FINDER_COMMAND);
            if (result.getExitCode() != 0) {
                throw new SshException(ExaError.messageBuilder("E-ETC-51")
                        .message("Failed to find a free TCP port in the Exasol database: {{error}}")
                        .parameter("error", result.getStderr()).ticketMitigation().toString());
            }
            return Integer.parseInt(result.getStdout().trim());
        } catch (final IOException | NumberFormatException exception) {
            throw new SshException(ExaError.messageBuilder("E-ETC-51")
                    .message("Failed to find a free TCP port in the Exasol database.")
                    .ticketMitigation().toString(), exception);
        }
    }

    @Override
    public void close() {
        this.ssh.disconnect();
    }
}
