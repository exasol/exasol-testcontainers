package com.exasol.containers.ssh;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.exasol.containers.ssh.RemoteExecutor.Result;
import com.exasol.containers.ssh.RemoteFileParser.LineMatcher;
import com.jcraft.jsch.*;

/**
 * Create an SSH connection and execute commands remotely, read remote files and search remote files for lines matching
 * a given {@link LineMatcher}.
 */
public class Ssh {

    private final Session session;
    private Charset charset = StandardCharsets.UTF_8;

    /**
     * Create a new instance of {@link Ssh}.
     *
     * @param session to use
     * @throws JSchException
     */
    public Ssh(final Session session) throws JSchException {
        this.session = session;
    }

    /**
     * Disconnect ssh
     */
    public void disconnect() {
        this.session.disconnect();
    }

    /**
     * @param charset character set to use for following commands.
     * @return this for fluent programming
     */
    public Ssh withCharset(final Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * Execute a command remotely via SSH.
     *
     * @param command command to execute remotely
     * @return {@link Result} for remote execution the specified command
     * @throws JSchException
     * @throws IOException
     */
    public Result execute(final String command) throws JSchException, IOException {
        return createRemoteExecutor().execute(command);
    }

    /**
     * Read a remote file via SSH.
     *
     * @param path path of the remote file
     * @return Contents of the remote file.
     * @throws IOException
     * @throws JSchException
     */
    public String readRemoteFile(final String path) throws IOException, JSchException {
        return createRemoteFileReader().read(path);
    }

    /**
     * Parses a remote file for a line accepted by a given {@link LineMatcher}.
     *
     * @param path        path of the remote file.
     * @param lineMatcher instance of {@link LineMatcher} to identify matching lines.
     * @return matching line or null.
     * @throws IOException
     * @throws JSchException
     */
    public String parseRemoteFile(final String path, final LineMatcher lineMatcher) throws IOException, JSchException {
        return createRemoteFileParser().findMatch(path, lineMatcher);
    }

    /**
     * @return character set currently used for all remote commands
     */
    public Charset getCharset() {
        return this.charset;
    }

    RemoteExecutor createRemoteExecutor() {
        return new RemoteExecutor(this);
    }

    RemoteFileReader createRemoteFileReader() {
        return new RemoteFileReader(this);
    }

    RemoteFileParser createRemoteFileParser() {
        return new RemoteFileParser(this);
    }

    Channel openChannel(final String type) throws JSchException {
        if (!this.session.isConnected()) {
            final Retry<JSchException> retry = new Retry<>(JSchException.class, Duration.ofSeconds(60));
            retry.retry(() -> this.session.connect());
        }
        return this.session.openChannel(type);
    }
}
