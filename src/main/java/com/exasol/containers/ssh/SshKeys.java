package com.exasol.containers.ssh;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import org.testcontainers.images.builder.Transferable;

import com.jcraft.jsch.*;

/**
 * Generates and wraps an SSH key pair with some convenience methods and provides a builder for a new
 * {@link com.jcraft.jsch.Session}.
 */
public class SshKeys {

    /**
     * @return {@link Builder} for a new instance of {@link SshKeys}.
     */
    public static Builder builder() {
        return new Builder();
    }

    private final KeyPair keyPair;
    private final byte[] passphrase;

    /**
     * Create a new instance of {@link SshKeys}
     *
     * @param keyPair pair of public and private key to use
     */
    SshKeys(final KeyPair keyPair) {
        this.keyPair = keyPair;
        this.passphrase = null;
    }

    /**
     * @return {@link Transferable} that may be copied as file to the docker container
     */
    public Transferable getPublicKeyTransferable() {
        return Transferable.of(getPublicKey());
    }

    byte[] getPublicKey() {
        final ByteArrayOutputStream ba = new ByteArrayOutputStream();
        this.keyPair.writePublicKey(ba, "comment");
        return ba.toByteArray();
    }

    byte[] getPrivateKey() {
        final ByteArrayOutputStream ba = new ByteArrayOutputStream();
        this.keyPair.writePrivateKey(ba);
        return ba.toByteArray();
    }

    byte[] getPassphrase() {
        return this.passphrase;
    }

    /**
     * @return new instance of {@link SessionBuilder} set up with public and private key of the current {@link SshKeys}.
     */
    public IdentityProvider getIdentityProvider() {
        return IdentityProvider.builder() //
                .identityName("comment") //
                .publicKey(this.keyPair.getPublicKeyBlob()) //
                .privateKey(getPrivateKey()) //
                .passphrase(this.passphrase) //
                .build();
    }

    /**
     * Manage {@link KeyPair} persistently. If available then read the keys from the specified files otherwise generate
     * a new pair of keys and write it to the specified files for next time.
     */
    public static class Builder {
        Path priv;
        Path pub;

        /**
         * @param path file for private key
         * @return this for fluent programming
         */
        public Builder privateKey(final Path path) {
            this.priv = path;
            return this;
        }

        /**
         * @param path file for public key
         * @return this for fluent programming
         */
        public Builder publicKey(final Path path) {
            this.pub = path;
            return this;
        }

        /**
         * @return new instance of {@link SshKeys}
         * @throws JSchException if generation of key pair failed
         * @throws IOException   if file access failed
         */
        public SshKeys build() throws IOException, JSchException {
            return new SshKeys(createKeyPair());
        }

        KeyPair createKeyPair() throws IOException, JSchException {
            if (Files.exists(this.priv) && Files.exists(this.pub)) {
                return KeyPair.load(new JSch(), read(this.priv), read(this.pub));
            }
            return writeToFiles(KeyPair.genKeyPair(new JSch(), KeyPair.RSA));
        }

        private byte[] read(final Path path) throws IOException {
            try (InputStream stream = Files.newInputStream(path)) {
                return stream.readAllBytes();
            }
        }

        private KeyPair writeToFiles(final KeyPair keys) throws IOException {
            try (OutputStream stream = Files.newOutputStream(this.priv)) {
                keys.writePrivateKey(stream);
            }
            try (OutputStream stream = Files.newOutputStream(this.pub)) {
                keys.writePublicKey(stream, null);
            }
            return keys;
        }
    }
}
