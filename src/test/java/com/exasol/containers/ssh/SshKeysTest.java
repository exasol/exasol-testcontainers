package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.testcontainers.images.builder.Transferable;

import com.jcraft.jsch.*;

@ExtendWith(MockitoExtension.class)
class SshKeysTest {
    private static final byte[] PUBLIC_KEY = "public key".getBytes();
    private static final byte[] PRIVATE_KEY = "private key".getBytes();

    @Mock
    KeyPair keyPairMock;

    @TempDir
    Path tempDir;

    @Test
    void privateKey() {
        final SshKeys testee = testee(null, PRIVATE_KEY);
        assertThat(testee.getPrivateKey(), equalTo(PRIVATE_KEY));
    }

    @Test
    void publicKey() {
        final SshKeys testee = testee(PUBLIC_KEY, null);
        assertThat(testee.getPublicKey(), equalTo(PUBLIC_KEY));
    }

    @Test
    void transferable() {
        final SshKeys testee = testee(PUBLIC_KEY, null);
        final Transferable transferable = testee.getPublicKeyTransferable();
        assertThat(transferable.getBytes(), equalTo(PUBLIC_KEY));
    }

    @Test
    void identityProvider() throws JSchException {
        when(this.keyPairMock.getPublicKeyBlob()).thenReturn(PUBLIC_KEY);
        final IdentityProvider ip = testee(null, PRIVATE_KEY).getIdentityProvider();
        final JSch jsch = mock(JSch.class);
        ip.addIdentityTo(jsch);
        verify(jsch).addIdentity("comment", PRIVATE_KEY, PUBLIC_KEY, null);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void fileStorage(final boolean deleteFiles) throws Exception {
        final Path priv = this.tempDir.resolve("private_key");
        final Path pub = this.tempDir.resolve("public_key");
        final SshKeys.Builder builder = SshKeys.builder().privateKey(priv).publicKey(pub);
        final SshKeys k1 = builder.build();
        if (deleteFiles) {
            Files.delete(priv);
        }
        final SshKeys k2 = builder.build();
        if (deleteFiles) {
            assertThat(k1.getPrivateKey(), not(equalTo(k2.getPrivateKey())));
            assertThat(k1.getPublicKey(), not(equalTo(k2.getPublicKey())));
        } else {
            assertThat(k1.getPrivateKey(), equalTo(k2.getPrivateKey()));
            assertThat(k1.getPublicKey(), equalTo(k2.getPublicKey()));
        }
    }

    private SshKeys testee(final byte[] publicKey, final byte[] privateKey) {
        if (publicKey != null) {
            doAnswer(i -> write(i, publicKey)) //
                    .when(this.keyPairMock).writePublicKey(any(OutputStream.class), anyString());
        }
        if (privateKey != null) {
            doAnswer(i -> write(i, privateKey)) //
                    .when(this.keyPairMock).writePrivateKey(any(OutputStream.class));
        }
        return new SshKeys(this.keyPairMock);
    }

    private Answer<?> write(final InvocationOnMock invocation, final byte[] key) throws IOException {
        ((OutputStream) invocation.getArgument(0)).write(key);
        return null;
    }
}
