package com.exasol.containers.ssh;

import java.io.*;

import com.jcraft.jsch.*;

class RemoteExecutor {

    private final Ssh ssh;

    RemoteExecutor(final Ssh ssh) {
        this.ssh = ssh;
    }

    Result execute(final String command) throws IOException, JSchException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Channel channel = this.ssh.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        final InputStream in = channel.getInputStream();
        channel.connect();

        final byte[] buf = new byte[1024];
        Result result;
        while (true) {
            while (in.available() > 0) {
                final int i = in.read(buf, 0, 1024);
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
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException exception) {
            }
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
}
