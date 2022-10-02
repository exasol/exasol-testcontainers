package com.exasol.containers.ssh;

import java.io.*;

import com.jcraft.jsch.*;

/**
 * Execute commands remotely via SSH
 */
public class RemoteExecutor {

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

    Result execute(final String command) throws IOException, JSchException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Channel channel = this.ssh.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        final InputStream in = channel.getInputStream();
        channel.connect();

        final byte[] buf = new byte[BUFFER_SIZE];
        Result result;
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
                result = new Result(channel.getExitStatus(), out.toString(this.ssh.getCharset()));
                break;
            }
            this.sleeper.sleep(1000);
        }
        channel.disconnect();
        return result;
    }

    /**
     * Result of executing a remote command remotely via SSH
     */
    public static class Result {
        private final int exitCode;
        private final String stdOut;

        /**
         * @param exitCode exit code returned by the command
         * @param stdOut   output written by the command to stdout
         */
        public Result(final int exitCode, final String stdOut) {
            this.stdOut = stdOut;
            this.exitCode = exitCode;
        }

        /**
         * @return exit code returned by the command
         */
        public int getExitCode() {
            return this.exitCode;
        }

        /**
         * @return output written by the command to stdout
         */
        public String getStdout() {
            return this.stdOut;
        }
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
