package com.exasol.containers.ssh;

import java.io.*;
import java.nio.charset.Charset;

import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.ExecResultFactory;

import com.exasol.containers.ssh.FileVisitor.State;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

/**
 * Execute commands remotely via SSH
 */
class RemoteExecutor {

    private static final int BUFFER_SIZE = 1024;
    private final byte[] buf = new byte[BUFFER_SIZE];

    private final Ssh ssh;
    private final Sleeper sleeper;

    private InputStream in;
    private ByteArrayOutputStream err;
    private ByteArrayOutputStream out;

    RemoteExecutor(final Ssh ssh) {
        this(ssh, new Sleeper());
    }

    RemoteExecutor(final Ssh ssh, final Sleeper sleeper) {
        this.ssh = ssh;
        this.sleeper = sleeper;
    }

    ExecResult execute(final Charset charset, final String... command) throws IOException {
        try {
            try (SshConnection connection = connect(this.ssh, command)) {
                while (process(connection) == State.CONTINUE) {
                    // continue
                }
                return result(connection, charset);
            }
        } catch (final JSchException exception) {
            throw new IOException("SSH execution failed", exception);
        }
    }

    SshConnection connect(final Ssh ssh, final String... command) throws IOException, JSchException {
        final ChannelExec channel = (ChannelExec) ssh.openChannel("exec");
        channel.setCommand(String.join(" ", command));
        this.in = channel.getInputStream();
        this.err = new ByteArrayOutputStream();
        channel.setErrStream(this.err);
        this.out = new ByteArrayOutputStream();
        return new SshConnection(channel);
    }

    State process(final SshConnection connection) throws IOException {
        while (this.in.available() > 0) {
            final int i = this.in.read(this.buf, 0, BUFFER_SIZE);
            if (i < 0) {
                break;
            }
            this.out.write(this.buf, 0, i);
        }

        if (!connection.channel().isClosed()) {
            this.sleeper.sleep(1000);
            return State.CONTINUE;
        }

        if (this.in.available() > 0) {
            return State.CONTINUE;
        }
        return State.COMPLETED;
    }

    ExecResult result(final SshConnection connection, final Charset charset) {
        return ExecResultFactory.result( //
                connection.channel().getExitStatus(), //
                this.out.toString(charset), //
                this.err.toString(charset));
    }

    static class Sleeper {
        void sleep(final int millis) {
            try {
                Thread.sleep(millis);
            } catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
