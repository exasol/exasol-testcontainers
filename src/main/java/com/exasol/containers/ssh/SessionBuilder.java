package com.exasol.containers.ssh;

import java.util.Hashtable;

import com.jcraft.jsch.*;

/**
 * Builder for {@link com.jcraft.jsch.Session}
 */
public class SessionBuilder {
    // session parameters
    private String user = "root";
    private String host = "192.168.1.2";
    private int port = 49182;

    // identity parameters
    private String identity;
    private byte[] privateKey;
    private byte[] publicKey;
    private byte[] passPhrase = null;
    private final Hashtable<String, String> config = new Hashtable<>();

    /**
     * Create new instance of {@link SessionBuilder}
     */
    public SessionBuilder() {
    }

    /**
     * @param value identity to be used by the session
     * @return instance of this for fluent programming
     */
    public SessionBuilder identity(final String value) {
        this.identity = value;
        return this;
    }

    /**
     * @param value public key to be used by the session
     * @return instance of this for fluent programming
     */
    public SessionBuilder publicKey(final byte[] value) {
        this.publicKey = value;
        return this;
    }

    /**
     * @param value private key to be used by the session
     * @return instance of this for fluent programming
     */
    public SessionBuilder privateKey(final byte[] value) {
        this.privateKey = value;
        return this;
    }

    /**
     * @param value passphrase to be used by the session
     * @return instance of this for fluent programming
     */
    public SessionBuilder passphrase(final byte[] value) {
        this.passPhrase = value;
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
     */
    public Session build() throws JSchException {
        final JSch jsch = new JSch();
        jsch.addIdentity(this.identity, this.privateKey, this.publicKey, this.passPhrase);
        final Session session = jsch.getSession(this.user, this.host, this.port);
        session.setConfig(this.config);
        return session;
    }
}
