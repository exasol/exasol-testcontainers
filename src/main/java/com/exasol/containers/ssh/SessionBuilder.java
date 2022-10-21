package com.exasol.containers.ssh;

import java.util.*;

import com.jcraft.jsch.*;

/**
 * Builder for {@link com.jcraft.jsch.Session}
 */
public class SessionBuilder {
    private String user;
    private String host;
    private int port;
    private IdentityProvider identityProvider;
    private final Map<String, String> config = new HashMap<>();

    /**
     * Create new instance of {@link SessionBuilder}
     */
    public SessionBuilder() {
        // Empty by intention
    }

    /**
     * @param value identity provider to use for the session
     * @return instance of this for fluent programming
     */
    public SessionBuilder identity(final IdentityProvider value) {
        this.identityProvider = value;
        return this;
    }

    /**
     * @param value user to be used by the session
     * @return instance of this for fluent programming
     */
    public SessionBuilder user(final String value) {
        this.user = value;
        return this;
    }

    /**
     * @param value host to be used by the session
     * @return instance of this for fluent programming
     */
    public SessionBuilder host(final String value) {
        this.host = value;
        return this;
    }

    /**
     * @param value port to be used by the session
     * @return instance of this for fluent programming
     */
    public SessionBuilder port(final int value) {
        this.port = value;
        return this;
    }

    /**
     * Add an entry to this session's configuration.
     *
     * @param key   key of the entry
     * @param value value of the entry
     * @return instance of this for fluent programming
     */
    public SessionBuilder config(final String key, final String value) {
        this.config.put(key, value);
        return this;
    }

    /**
     * @return new instance of SSH {@link Session}
     * @throws SshException if session creation fails
     */
    public Session build() throws SshException {
        try {
            final JSch jsch = new JSch();
            this.identityProvider.addIdentityTo(jsch);
            final Session session = jsch.getSession(this.user, this.host, this.port);
            session.setConfig(new Hashtable<>(this.config));
            return session;
        } catch (final JSchException exception) {
            throw new SshException("Failed to create SSH session", exception);
        }
    }

    /**
     * @return user added to this {@link SessionBuilder}
     */
    public String getUser() {
        return this.user;
    }

    /**
     * @return host added to this {@link SessionBuilder}
     */
    public String getHost() {
        return this.host;
    }

    /**
     * @return port added to this {@link SessionBuilder}
     */
    public int getPort() {
        return this.port;
    }
}
