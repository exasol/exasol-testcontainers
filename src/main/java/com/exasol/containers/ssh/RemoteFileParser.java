package com.exasol.containers.ssh;

import java.io.*;
import java.nio.charset.Charset;

import com.jcraft.jsch.JSchException;

/**
 * Parses specified remote file for a line accepted by a given {@link LineMatcher}.
 */
class RemoteFileParser {

    private final FileVisitor visitor;
    private LineMatcher lineMatcher;

    RemoteFileParser(final Ssh ssh) {
        this.visitor = new FileVisitor(ssh);
    }

    String findMatch(final String rfile, final LineMatcher lineMatcher) throws IOException, JSchException {
        this.lineMatcher = lineMatcher;
        return this.visitor.visit(rfile, this::process);
    }

    String process(final InputStream stream, final Charset charset, final long filesize) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (this.lineMatcher.matches(line)) {
                    return line;
                }
            }
        }
        return null;
    }

    /**
     * Interface for line matcher filtering lines of remote file
     */
    @FunctionalInterface
    public interface LineMatcher {
        boolean matches(String line);
    }
}
