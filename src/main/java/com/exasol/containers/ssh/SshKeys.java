package com.exasol.containers.ssh;

import java.io.ByteArrayOutputStream;

import org.testcontainers.images.builder.Transferable;

import com.jcraft.jsch.*;

/**
 * Generates and wraps an SSH key pair with some convenience methods and provides a builder for a new
 * {@link com.jcraft.jsch.Session}.
 */
public class SshKeys {
    private final KeyPair keyPair;
    private final byte[] passphrase;

    /**
     * Create a new instance of {@link @SshKeys}
     *
     * @throws JSchException
     */
    public SshKeys() throws JSchException {
        this.keyPair = KeyPair.genKeyPair(new JSch(), KeyPair.RSA);
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
     * @throws JSchException
     */
    public SessionBuilder getSessionBuilder() throws JSchException {
        return new SessionBuilder() //
                .identity("comment") //
                .publicKey(this.keyPair.getPublicKeyBlob()) //
                .privateKey(getPrivateKey()) //
                .passphrase(this.passphrase);
    }
}
