package com.exasol.containers.ssl;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.containers.*;

/**
 * This class allows reading the SSL certificate used by the Exasol container.
 */
public class CertificateProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateProvider.class);

    private final ExasolContainer<? extends ExasolContainer<?>> container;
    private final ContainerFileOperations fileOperations;

    public CertificateProvider(final ExasolContainer<? extends ExasolContainer<?>> container,
            final ContainerFileOperations fileOperations) {
        this.container = container;
        this.fileOperations = fileOperations;
    }

    /**
     * Reads and converts the self-signed SSL certificate used by the database in the container for database connections
     * and the RPC interface.
     *
     * @return the SSL certificate or an empty {@link Optional} when the certificate file does not exist.
     */
    public Optional<X509Certificate> getCertificate() {
        return readCertificate().map(this::parseCertificate);
    }

    private Optional<String> readCertificate() {
        final String certPath = this.container.getClusterConfiguration().getSslCertificatePath();
        try {
            final String certContent = this.fileOperations.readFile(certPath, StandardCharsets.UTF_8);
            LOGGER.debug("Read certificate from file {} contains {} chars", certPath, certContent.length());
            return Optional.of(certContent);
        } catch (final ExasolContainerException e) {
            LOGGER.warn("Error reading certificate: {} {}", e.getClass(), e.getMessage());
            return Optional.empty();
        }
    }

    private X509Certificate parseCertificate(final String certContent) {
        try (final InputStream is = new ByteArrayInputStream(certContent.getBytes(StandardCharsets.UTF_8))) {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final X509Certificate certificate = (X509Certificate) cf.generateCertificate(is);
            logCertificate(certificate);
            return certificate;
        } catch (final CertificateException | IOException e) {
            throw new IllegalStateException("Error parsing certificate '" + certContent + "'", e);
        }
    }

    private void logCertificate(final X509Certificate certificate) {
        if (!LOGGER.isDebugEnabled()) {
            return;
        }
        LOGGER.debug("Certificate type: {}, version: {}", certificate.getType(), certificate.getVersion());
        LOGGER.debug("Certificate issuerDN: '{}', subjectDN: '{}'", certificate.getIssuerDN(),
                certificate.getSubjectDN());
        LOGGER.debug("Certificate sigAlgName: {}, sigAlgOID: {}", certificate.getSigAlgName(),
                certificate.getSigAlgOID());
        LOGGER.debug("Certificate valid not before: {}, not after: {}", certificate.getNotBefore(),
                certificate.getNotAfter());
    }

    /**
     * Get the SHA-256 checksum of the encoded certificate as a hex string. This is required as a fingerprint when
     * connecting to the database via JDBC using property {@code validateservercertificate=1}.
     *
     * @return the SHA-256 checksum of the certificate as a hex string or an empty {@link Optional} when the certificate
     *         file does not exist
     */
    public Optional<String> getSha256Fingerprint() {
        return getEncodedCertificate().map(CertificateProvider::sha256).map(CertificateProvider::bytesToHex);
    }

    private Optional<byte[]> getEncodedCertificate() {
        final Optional<X509Certificate> certificate = getCertificate();
        if (certificate.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(certificate.get().getEncoded());
        } catch (final CertificateEncodingException e) {
            throw new IllegalStateException("Error encoding certificate", e);
        }
    }

    private static byte[] sha256(final byte[] der) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(der);
            return md.digest();
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Error creating message digest");
        }
    }

    private static String bytesToHex(final byte[] bytes) {
        return new BigInteger(1, bytes).toString(16);
    }
}
