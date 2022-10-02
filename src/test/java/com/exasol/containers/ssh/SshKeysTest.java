package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
