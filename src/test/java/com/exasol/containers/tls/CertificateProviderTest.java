package com.exasol.containers.tls;

import static com.exasol.testutil.ExceptionAssertions.assertThrowsWithMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.config.ClusterConfiguration;
import com.exasol.containers.ContainerFileOperations;
import com.exasol.containers.ExasolContainerException;

@ExtendWith(MockitoExtension.class)
class CertificateProviderTest {
    private static final String INVALID_CERTIFICATE_CONTENT = "invalid certificate content";
    private static final String TLS_CERT_PATH = "tls cert path";
    @Mock
    private ContainerFileOperations fileOperationsMock;
    @Mock
    private Supplier<Optional<ClusterConfiguration>> configSupplierMock;
    @Mock
    private ClusterConfiguration clusterConfigMock;

    private CertificateProvider certificateProvider;

    @BeforeEach
    void setup() {
        this.certificateProvider = new CertificateProvider(this.configSupplierMock, this.fileOperationsMock);
    }

    @Test
    void testGetCertificateSucceeds() throws ExasolContainerException {
        simulateTlsCert(readResource("/ssl.crt"));
        final Optional<X509Certificate> certificate = this.certificateProvider.getCertificate();
        assertThat(certificate.isPresent(), is(true));
        assertThat(certificate.get(), notNullValue());
        assertThat(certificate.get().getIssuerX500Principal().getName(), equalTo("CN=exacluster.local"));
    }

    private void simulateTlsCert(final String certContent) throws ExasolContainerException {
        when(this.configSupplierMock.get()).thenReturn(Optional.of(this.clusterConfigMock));
        when(this.clusterConfigMock.getTlsCertificatePath()).thenReturn(TLS_CERT_PATH);
        when(this.fileOperationsMock.readFile(TLS_CERT_PATH, StandardCharsets.UTF_8)).thenReturn(certContent);
    }

    private String readResource(final String resourceName) {
        try {
            final byte[] allBytes = ((BufferedInputStream) getClass().getResource(resourceName).getContent())
                    .readAllBytes();
            return new String(allBytes, StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            throw new AssertionError("Error reading resource " + resourceName, exception);
        }
    }

    @Test
    void testGetCertificateReturnsEmptyOptionalForMissingConfiguration() throws ExasolContainerException {
        when(this.configSupplierMock.get()).thenReturn(Optional.empty());
        final Optional<X509Certificate> certificate = this.certificateProvider.getCertificate();
        assertThat(certificate.isPresent(), is(false));
    }

    @Test
    void testGetCertificateReturnsEmptyOptionalForMissingFile() throws ExasolContainerException {
        when(this.configSupplierMock.get()).thenReturn(Optional.of(this.clusterConfigMock));
        when(this.clusterConfigMock.getTlsCertificatePath()).thenReturn(TLS_CERT_PATH);
        when(this.fileOperationsMock.readFile(TLS_CERT_PATH, StandardCharsets.UTF_8))
                .thenThrow(new ExasolContainerException("expected", null));
        final Optional<X509Certificate> certificate = this.certificateProvider.getCertificate();
        assertThat(certificate.isPresent(), is(false));
    }

    @Test
    void testGetCertificateFailsForInvalidCertificate() throws ExasolContainerException {
        simulateTlsCert(INVALID_CERTIFICATE_CONTENT);
        assertThrowsWithMessage(IllegalStateException.class, () -> this.certificateProvider.getCertificate(),
                startsWith("F-ETC-7: Error parsing certificate '" + INVALID_CERTIFICATE_CONTENT + "'."));
    }

    @Test
    void testGetCertificateReturnsEmptyOptionalWhenNoConfigurationAvailable() throws ExasolContainerException {
        when(this.configSupplierMock.get()).thenReturn(Optional.empty());
        assertThat(this.certificateProvider.getCertificate().isEmpty(), is(true));
    }

    @Test
    void testGetSha256Fingerprint() throws ExasolContainerException {
        simulateTlsCert(readResource("/ssl.crt"));
        final Optional<String> fingerprint = this.certificateProvider.getSha256Fingerprint();
        assertThat(fingerprint.isPresent(), is(true));
        assertThat(fingerprint.get(), equalTo("74d65659d86a6316b2b1ec395e83d8136cd0b26a121da4d128c4ba84f2ebf6d1"));
    }

    @Test
    void testGetSha256FingerprintReturnsEmptyOptionalWhenNoConfigurationAvailable() throws ExasolContainerException {
        when(this.configSupplierMock.get()).thenReturn(Optional.empty());
        assertThat(this.certificateProvider.getSha256Fingerprint().isEmpty(), is(true));
    }

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
    void testBytesToHexAddsPaddingToFingerprintWhenFirstByteIsSingleDigit() {
        assertBytesToHex(
                new byte[] { 0x2, 0x29, (byte) 0xbd, (byte) 0xb8, 0x15, (byte) 0xfd, (byte) 0xfe, (byte) 0xc1, 0x73,
                        0x18, 0x18, (byte) 0xb2, 0x6c, (byte) 0xaf, 0x44, 0x21, 0x59, (byte) 0x88, 0x35, (byte) 0xce,
                        0x44, 0x28, (byte) 0x8c, 0x28, (byte) 0xf6, (byte) 0xe5, (byte) 0x9c, 0x71, 0x54, (byte) 0x81,
                        0x2a, (byte) 0xf1 }, //
                "0229bdb815fdfec1731818b26caf4421598835ce44288c28f6e59c7154812af1");
    }

    @Test
    void testBytesToHexWithPaddingNoPaddingNecessary() {
        assertBytesToHexWithPadding(new byte[] { 1, 2, 3 }, 3, "010203");
    }

    @Test
    void testBytesToHexWithPaddingPaddingRequired() {
        assertBytesToHexWithPadding(new byte[] { 1, 2, 3 }, 5, "0000010203");
    }

    @Test
    void testBytesToHexWithPaddingPaddingRequiredFirstByteZero() {
        assertBytesToHexWithPadding(new byte[] { 0, 1, 2, 3 }, 5, "0000010203");
    }

    @Test
    void testBytesToHexWithPaddingAddsPaddingWhenFirstByteIsZero() {
        assertBytesToHexWithPadding(
                new byte[] { 0x0, 0x29, (byte) 0xbd, (byte) 0xb8, 0x15, (byte) 0xfd, (byte) 0xfe, (byte) 0xc1, 0x73,
                        0x18, 0x18, (byte) 0xb2, 0x6c, (byte) 0xaf, 0x44, 0x21, 0x59, (byte) 0x88, 0x35, (byte) 0xce,
                        0x44, 0x28, (byte) 0x8c, 0x28, (byte) 0xf6, (byte) 0xe5, (byte) 0x9c, 0x71, 0x54, (byte) 0x81,
                        0x2a, (byte) 0xf1 }, //
                32, //
                "0029bdb815fdfec1731818b26caf4421598835ce44288c28f6e59c7154812af1");
    }

    private void assertBytesToHex(final byte[] bytes, final String expectedHex) {
        assertThat(CertificateProvider.bytesToHex(bytes), equalTo(expectedHex));
    }

    private void assertBytesToHexWithPadding(final byte[] bytes, final int padding, final String expectedHex) {
        assertThat(CertificateProvider.bytesToHexWithPadding(bytes, padding), equalTo(expectedHex));
    }

    @Test
    void testSha256EmptyData() {
        assertSha256(new byte[0], "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    }

    @Test
    void testSha256() {
        assertSha256("test".getBytes(), "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
    }

    private void assertSha256(final byte[] data, final String expectedSha256Hex) {
        final byte[] sha256 = CertificateProvider.sha256(data);
        assertThat(CertificateProvider.bytesToHex(sha256), equalTo(expectedSha256Hex));
    }
}
