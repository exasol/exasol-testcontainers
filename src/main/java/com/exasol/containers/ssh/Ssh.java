package com.exasol.containers.ssh;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.ssh.RemoteFileParser.LineMatcher;
import com.jcraft.jsch.*;

/**
 * Enable to create an SSH connection in order to
 * <ul>
 * <li>execute commands remotely</li>
 * <li>read remote files</li>
 * <li>search remote files for lines matching a given {@link LineMatcher}.</li>
 * </ul>
 */
public class Ssh {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ssh.class);

    private final Session session;
    private Charset charset = StandardCharsets.UTF_8;

    /**
     * Create a new instance of {@link Ssh}.
     *
     * @param session to use
     */
    public Ssh(final Session session) {
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
     * @param command command and arguments to execute remotely
     * @return {@link ExecResult} for remote execution the specified command
     * @throws IOException if remote execution fails
     */
    public ExecResult execute(final String... command) throws IOException {
        return execute(this.charset, command);
    }

    /**
     * Execute a command remotely via SSH.
     *
     * @param charset character set to use for reading command's streams standard out and standard error
     * @param command command and arguments to execute remotely
     * @return {@link ExecResult} for remote execution the specified command
     * @throws IOException if remote execution fails
     */
    public ExecResult execute(final Charset charset, final String... command) throws IOException {
        return createRemoteExecutor().execute(charset, command);
    }

    /**
     * Read a remote file via SSH.
     *
     * @param path path of the remote file
     * @return Contents of the remote file.
     * @throws IOException   in case read operation fails
     * @throws JSchException in case opening SSH channel fails
     */
    public String readRemoteFile(final String path) throws IOException, JSchException {
        return createRemoteFileReader().read(path);
    }

    /**
     * Copy a local file to the container via SSH.
     *
     * @param local  path to local file
     * @param remote path to remote file
     * @throws IOException   in case write operation fails
     * @throws JSchException in case opening SSH channel fails
     */
    public void writeRemoteFile(final Path local, final String remote) throws IOException, JSchException {
        createRemoteFileWriter().write(local, remote);
    }

    /**
     * Parses a remote file for a line accepted by a given {@link LineMatcher}.
     *
     * @param path        path of the remote file.
     * @param lineMatcher instance of {@link LineMatcher} to identify matching lines.
     * @return matching line or null.
     * @throws IOException   in case parse operation fails
     * @throws JSchException in case opening SSH channel fails
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

    RemoteFileWriter createRemoteFileWriter() {
        return new RemoteFileWriter(this);
    }

    Channel openChannel(final String type) throws JSchException {
        if (!this.session.isConnected()) {
            connectWithRetry();
        }
        return this.session.openChannel(type);
    }

    private void connectWithRetry() throws JSchException {
        final Duration timeout = Duration.ofSeconds(60);
        LOGGER.info("Trying to open SSH channel to docker container with timeout {}", timeout);
        final Retry<JSchException> retry = new Retry<>(JSchException.class, timeout);
        retry.retry(this.session::connect);
        LOGGER.info("SSH channel successfully opened");
    }
}
