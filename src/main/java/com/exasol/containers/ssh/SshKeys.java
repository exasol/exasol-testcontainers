package com.exasol.containers.ssh;

import java.io.ByteArrayOutputStream;

import org.testcontainers.images.builder.Transferable;

import com.jcraft.jsch.*;

/**
 * Generates and wraps an SSH key pair with some convenience methods and provides a builder for a new
 * {@link com.jcraft.jsch.Session}.
 */
public class SshKeys {

    /**
     * @return a new instance of {@link @SshKeys}
     * @throws JSchException if generation of key pair failed
     */
    public static SshKeys create() throws JSchException {
        return new SshKeys(KeyPair.genKeyPair(new JSch(), KeyPair.RSA));
    }

    private final KeyPair keyPair;
    private final byte[] passphrase;

    /**
     * Create a new instance of {@link @SshKeys}
     *
     * @param keyPair pair of public and private key to use
     */
    public SshKeys(final KeyPair keyPair) {
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
}
