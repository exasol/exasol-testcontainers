package com.exasol.containers.tls;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class CertificateProviderTest {

    @Test
    void testBytesToHexEmptyInput() {
        assertBytesToHex(new byte[] {}, "00");
    }

    @Test
    void testBytesToHex() {
        assertBytesToHex(new byte[] { 0x10, 0x20, 0x30 }, "102030");
    }

    @Test
    void testBytesToHexAddsPadding() {
        assertBytesToHex(new byte[] { 0x1, 0x20, 0x30 }, "012030");
    }

    @Test
    void testBytesToHexAddsPaddingToFingerprint() {
        assertBytesToHex(
                new byte[] { 0x2, 0x29, (byte) 0xbd, (byte) 0xb8, 0x15, (byte) 0xfd, (byte) 0xfe, (byte) 0xc1, 0x73,
                        0x18, 0x18, (byte) 0xb2, 0x6c, (byte) 0xaf, 0x44, 0x21, 0x59, (byte) 0x88, 0x35, (byte) 0xce,
                        0x44, 0x28, (byte) 0x8c, 0x28, (byte) 0xf6, (byte) 0xe5, (byte) 0x9c, 0x71, 0x54, (byte) 0x81,
                        0x2a, (byte) 0xf1 }, //
                "0229bdb815fdfec1731818b26caf4421598835ce44288c28f6e59c7154812af1");
    }

    private void assertBytesToHex(final byte[] bytes, final String expectedHex) {
        assertThat(CertificateProvider.bytesToHex(bytes), equalTo(expectedHex));
    }
}
