package com.exasol.containers.ssh;

import static org.testcontainers.containers.ExecResultFactory.result;

import java.io.*;
import java.nio.charset.Charset;

import org.testcontainers.containers.Container.ExecResult;

import com.jcraft.jsch.*;

/**
 * Execute commands remotely via SSH
 */
class RemoteExecutor {

    private static final int BUFFER_SIZE = 1024;

    private final Ssh ssh;
    private final Sleeper sleeper;

    RemoteExecutor(final Ssh ssh) {
        this(ssh, new Sleeper());
    }

    RemoteExecutor(final Ssh ssh, final Sleeper sleeper) {
        this.ssh = ssh;
        this.sleeper = sleeper;
    }

    ExecResult execute(final Charset charset, final String... command) throws IOException {
        try {
            return executeInternal(charset, command);
        } catch (final JSchException exception) {
            throw new IOException("SSH execution failed", exception);
        }
    }

    ExecResult executeInternal(final Charset charset, final String... command) throws IOException, JSchException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Channel channel = this.ssh.openChannel("exec");
        ((ChannelExec) channel).setCommand(String.join(" ", command));

        final InputStream in = channel.getInputStream();

        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        ((ChannelExec) channel).setErrStream(err);

        channel.connect();

        final byte[] buf = new byte[BUFFER_SIZE];
        ExecResult result;
        while (true) {
            while (in.available() > 0) {
                final int i = in.read(buf, 0, BUFFER_SIZE);
                if (i < 0) {
                    break;
                }
                out.write(buf, 0, i);
            }
            if (channel.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                result = result(channel.getExitStatus(), out.toString(charset), err.toString(charset));
                break;
            }
            this.sleeper.sleep(1000);
        }
        channel.disconnect();
        return result;
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
