package com.exasol.containers.ssh;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.*;

class FileVisitor {
    enum State {
        CONTINUE, COMPLETED, ERROR;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FileVisitor.class);
    private static final byte[] ZERO_BUFFER = { 0 };
    private final Ssh ssh;

    FileVisitor(final Ssh ssh) {
        this.ssh = ssh;
    }

    String visit(final String rfile, final ContentProcessor consumer) throws IOException, JSchException {
        final String command = String.format("scp -f '%s'", rfile.replace("'", "'\\''"));
        final Channel channel = this.ssh.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        final OutputStream out = channel.getOutputStream();
        final InputStream in = channel.getInputStream();
        channel.connect();
        String result = null;
        while (true) {
            sendZeroByte(out);
            if (checkAck(in) != 'C') {
                break;
            }
            readFilemode(in);
            final int filesize = readFilesize(in);
            readFilename(in);
            sendZeroByte(out);
            result = consumer.process(in, this.ssh.getCharset(), filesize);
            if (checkAck(in) != 0) {
                throw new SshException("ack != 0");
            }
        }

        channel.disconnect();
        return result;
    }

    static void sendZeroByte(final OutputStream stream) throws IOException {
        stream.write(ZERO_BUFFER, 0, 1);
        stream.flush();
    }

    private String readFilemode(final InputStream stream) throws IOException {
        final byte[] buf = new byte[5];
        stream.read(buf, 0, 5);
        return new String(buf, 0, 5);
    }

    private int readFilesize(final InputStream in) throws IOException {
        int filesize = 0;
        final byte[] buf = new byte[1];
        while (true) {
            if (in.read(buf, 0, 1) < 0) {
                // error
                return filesize;
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

    static int checkAck(final InputStream stream) throws IOException {
        final int b = stream.read();
        // b may be 0 for success,
        // 1 for error,
        // 2 for fatal error,
        // -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }

        if ((b == 1) || (b == 2)) {
            final StringBuilder sb = new StringBuilder();
            int c;
            do {
                c = stream.read();
                sb.append((char) c);
            } while (c != '\n');
            if ((b == 1) || (b == 2)) { // error or fatal error
                final String s = sb.toString();
                LOGGER.error("error {}", s);
            }
        }
        return b;
    }

    @FunctionalInterface
    interface ContentProcessor {
        String process(final InputStream stream, Charset charset, final int filesize) throws IOException;
    }
}
