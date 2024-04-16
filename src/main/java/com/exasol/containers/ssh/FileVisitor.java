package com.exasol.containers.ssh;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import com.jcraft.jsch.*;

class FileVisitor {
    enum State {
        CONTINUE, COMPLETED, ERROR;
    }

    private static final byte[] ZERO_BUFFER = { 0 };
    private final Ssh ssh;

    FileVisitor(final Ssh ssh) {
        this.ssh = ssh;
    }

    @SuppressWarnings("try") // auto-closeable resource connection is never referenced in body of corresponding try
                             // statement
    String visit(final String remoteFilePath, final ContentProcessor consumer) throws IOException, JSchException {
        final String command = String.format("scp -f '%s'", remoteFilePath.replace("'", "'\\''"));
        final Channel channel = this.ssh.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        final OutputStream out = channel.getOutputStream();
        final InputStream in = channel.getInputStream();
        try (SshConnection connection = new SshConnection(channel)) {
            String result = null;
            while (true) {
                sendZeroByte(out);
                if (checkAck(in) != 'C') {
                    return result;
                }
                readFileMode(in);
                final int fileSize = readFileSize(in);
                readFilename(in);
                sendZeroByte(out);
                result = consumer.process(in, this.ssh.getCharset(), fileSize);
                if (checkAck(in) != 0) {
                    throw new SshException("ack != 0");
                }
            }
        }
    }

    static void sendZeroByte(final OutputStream stream) throws IOException {
        stream.write(ZERO_BUFFER, 0, 1);
        stream.flush();
    }

    private String readFileMode(final InputStream stream) throws IOException {
        final byte[] buf = new byte[5];
        stream.read(buf, 0, 5);
        return new String(buf, 0, 5);
    }

    private int readFileSize(final InputStream in) throws IOException {
        int filesize = 0;
        final byte[] buf = new byte[1];
        while (true) {
            if (in.read(buf, 0, 1) < 0) {
                throw new SshException("Failed to read filesize");
            }
            if (buf[0] == ' ') {
                return filesize;
            }
            filesize = ((filesize * 10) + buf[0]) - '0';
        }
    }

    private String readFilename(final InputStream stream) throws IOException {
        final byte[] buf = new byte[1024];
        for (int i = 0;; i++) {
            stream.read(buf, i, 1);
            if (buf[i] == (byte) 0x0a) {
                return new String(buf, 0, i);
            }
        }
    }

    static String fileHeader(final Path file) throws IOException {
        return fileHeader('C', "0644", Files.size(file), file.getFileName().toString());
    }

    static String fileHeader(final char ack, final String mode, final long filesize, final String filename) {
        return String.format("%c%s %d %s%c", ack, mode, filesize, filename, 0xa);
    }

    private static final int ERROR = 1;
    private static final int FATAL_ERROR = 2;

    static int checkAck(final InputStream stream) throws IOException {
        final int b = stream.read();
        if ((b == ERROR) || (b == FATAL_ERROR)) {
            throw new SshException("Error " + getErrorMessage(stream));
        }
        return b;
    }

    static String getErrorMessage(final InputStream stream) throws IOException {
        final StringBuilder builder = new StringBuilder();
        int c = 0;
        while (c != 0x0a) {
            c = stream.read();
            builder.append((char) c);
        }
        return builder.toString();
    }

    @FunctionalInterface
    interface ContentProcessor {
        String process(final InputStream stream, Charset charset, final int filesize) throws IOException;
    }
}
