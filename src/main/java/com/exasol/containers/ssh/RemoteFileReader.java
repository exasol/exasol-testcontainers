package com.exasol.containers.ssh;

import java.io.*;
import java.nio.charset.Charset;

import com.exasol.containers.ssh.FileVisitor.State;
import com.jcraft.jsch.JSchException;

/**
 * Read remote file into string.
 */
class RemoteFileReader {

    private final FileVisitor visitor;

    RemoteFileReader(final Ssh ssh) {
        this.visitor = new FileVisitor(ssh);
    }

    String read(final String rfile) throws IOException, JSchException {
        return this.visitor.visit(rfile, this::process);
    }

    String process(final InputStream stream, final Charset charset, final int filesize) throws IOException {
        try (ByteArrayOutputStream fos = new ByteArrayOutputStream(filesize)) {
            final Reader reader = new Reader(filesize, fos);
            while (reader.read(stream) == State.CONTINUE) {
                // continue
            }
            return reader.output(charset);
        }
    }

    static class Reader {
        private static final int BUFFER_SIZE = 1024;
        private final byte[] buf = new byte[BUFFER_SIZE];

        private int filesize;
        private final ByteArrayOutputStream out;

        Reader(final int filesize, final ByteArrayOutputStream out) {
            this.filesize = filesize;
            this.out = out;
        }

        State read(final InputStream in) throws IOException {
            final int n = in.read(this.buf, 0, Math.min(this.filesize, this.buf.length));
            if (n < 0) {
                return State.ERROR;
            }
            this.out.write(this.buf, 0, n);
            this.filesize -= n;
            if (this.filesize == 0L) {
                return State.COMPLETED;
            }
            return State.CONTINUE;
        }

        String output(final Charset charset) {
            return this.out.toString(charset);
        }
    }
}
