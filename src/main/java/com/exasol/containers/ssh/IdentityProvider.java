package com.exasol.containers.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

/**
 * Enables to add an identity to a java secure channel {@link JSch}.
 */
public interface IdentityProvider {

    /**
     * @return builder for {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @param path path to file containing private key
     * @return IdentityProvider using private key from the specified file
     */
    public static IdentityProvider fromPathToPrivateKey(final String path) {
        return jsch -> jsch.addIdentity(path);
    }

    /**
     * Adds the identity represented by the current instance to java secure channel {@link JSch}.
     *
     * @param jsch java secure channel {@link JSch} to add identity to
     * @throws JSchException if adding identity fails.
     */
    void addIdentityTo(JSch jsch) throws JSchException;

    /**
     * Builder for {@link IdentityProvider}
     */
    public class Builder {
        private String identityName;
        private byte[] privateKey;
        private byte[] publicKey;
        private byte[] passPhrase = null;

        /**
         * @param value identity to be used by the session
         * @return instance of this for fluent programming
         */
        public Builder identityName(final String value) {
            this.identityName = value;
            return this;
        }

        /**
         * @param value public key to be used by the session
         * @return instance of this for fluent programming
         */
        public Builder publicKey(final byte[] value) {
            this.publicKey = value;
            return this;
        }

        /**
         * @param value private key to be used by the session
         * @return instance of this for fluent programming
         */
        public Builder privateKey(final byte[] value) {
            this.privateKey = value;
            return this;
        }

        /**
         * @param value passphrase to be used by the session
         * @return instance of this for fluent programming
         */
        public Builder passphrase(final byte[] value) {
            this.passPhrase = value;
            return this;
        }

        /**
         * @return new instance of {@link IdentityProvider}
         */
        public IdentityProvider build() {
            return jsch -> jsch.addIdentity(this.identityName, this.privateKey, this.publicKey, this.passPhrase);
        }
    }
}
