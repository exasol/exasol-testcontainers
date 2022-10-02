package com.exasol.containers.ssh;

import java.io.*;
import java.nio.charset.Charset;

import com.jcraft.jsch.JSchException;

/**
 * Read remote file into string.
 */
class RemoteFileReader {

    private static final int BUFFER_SIZE = 1024;
    private final FileVisitor visitor;

    RemoteFileReader(final Ssh ssh) {
        this.visitor = new FileVisitor(ssh);
    }

    String read(final String rfile) throws IOException, JSchException {
        return this.visitor.visit(rfile, this::process);
    }

    String process(final InputStream stream, final Charset charset, long filesize) throws IOException {
        try (ByteArrayOutputStream fos = new ByteArrayOutputStream((int) filesize)) {
            int n;
            final byte[] buf = new byte[BUFFER_SIZE];
            while (true) {
                if (buf.length < filesize) {
                    n = buf.length;
                } else {
                    n = (int) filesize;
                }
                n = stream.read(buf, 0, n);
                if (n < 0) {
                    // error
                    break;
                }
                fos.write(buf, 0, n);
                filesize -= n;
                if (filesize == 0L) {
                    break;
                }
            }
            return fos.toString(charset);
        }
    }
}
