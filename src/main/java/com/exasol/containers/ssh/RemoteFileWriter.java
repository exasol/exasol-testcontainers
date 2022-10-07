package com.exasol.containers.ssh;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import com.jcraft.jsch.*;

class RemoteFileWriter {

    private static final int BUFFER_SIZE = 1024;
    private final Ssh ssh;

    RemoteFileWriter(final Ssh ssh) {
        this.ssh = ssh;
    }

    void write(final Path local, final String remote) throws IOException, JSchException {
        final Channel channel = this.ssh.openChannel("exec");
        ((ChannelExec) channel).setCommand("scp -t " + remote);
        final OutputStream out = channel.getOutputStream();
        final InputStream in = channel.getInputStream();

        channel.connect();
        assertAckOrThrowException(in, "first");

        out.write(FileVisitor.fileHeader(local).getBytes());
        out.flush();

        assertAckOrThrowException(in, "second");

        try (InputStream fis = Files.newInputStream(local)) {
            final byte[] buf = new byte[BUFFER_SIZE];
            while (true) {
                final int len = fis.read(buf, 0, buf.length);
                if (len <= 0) {
                    break;
                }
                out.write(buf, 0, len);
            }
        }

        FileVisitor.sendZeroByte(out);
        assertAckOrThrowException(in, "final");
        out.close();
        channel.disconnect();
    }

    private void assertAckOrThrowException(final InputStream stream, final String messagePrefix)
            throws IOException, SshException {
        if (FileVisitor.checkAck(stream) != 0) {
            throw new SshException(messagePrefix + " ack != 0");
        }
    }
}
