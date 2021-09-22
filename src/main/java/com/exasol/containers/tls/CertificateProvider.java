package com.exasol.containers.tls;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.config.ClusterConfiguration;
import com.exasol.containers.*;

/**
 * This class allows reading the TLS certificate used by the Exasol container.
 */
public class CertificateProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateProvider.class);

    private final Supplier<Optional<ClusterConfiguration>> configProvider;
    private final ContainerFileOperations fileOperations;

    public CertificateProvider(final Supplier<Optional<ClusterConfiguration>> configProvider,
            final ContainerFileOperations fileOperations) {
        this.configProvider = configProvider;
        this.fileOperations = fileOperations;
    }

    CertificateProvider(final ExasolContainer<? extends ExasolContainer<?>> container,
            final ContainerFileOperations fileOperations) {
        this(() -> Optional.of(container.getClusterConfiguration()), fileOperations);
    }

    /**
     * Read and convert the self-signed TLS certificate used by the database in the container for database connections
     * and the RPC interface.
     *
     * @return the TLS certificate or an empty {@link Optional} when no configuration exists or the certificate file
     *         does not exist.
     */
    public Optional<X509Certificate> getCertificate() {
        return readCertificate().map(this::parseCertificate);
    }

    private Optional<String> readCertificate() {
        final Optional<ClusterConfiguration> configuration = this.configProvider.get();
        if (configuration.isEmpty()) {
            return Optional.empty();
        }
        final String certPath = configuration.get().getTlsCertificatePath();
        try {
            final String certContent = this.fileOperations.readFile(certPath, StandardCharsets.UTF_8);
            LOGGER.debug("Read certificate from file {} contains {} chars", certPath, certContent.length());
            return Optional.of(certContent);
        } catch (final ExasolContainerException exception) {
            LOGGER.info("Certificate does not exist yet, returning empty Optional. {} {}",
                    exception.getClass().getName(), exception.getMessage());
            return Optional.empty();
        }
    }

    private X509Certificate parseCertificate(final String certContent) {
        try (final InputStream is = new ByteArrayInputStream(certContent.getBytes(StandardCharsets.UTF_8))) {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final X509Certificate certificate = (X509Certificate) cf.generateCertificate(is);
            logCertificate(certificate);
            return certificate;
        } catch (final CertificateException | IOException exception) {
            throw new IllegalStateException(
                    messageBuilder("F-ETC-7").message("Error parsing certificate {{certificateContent}}.", certContent)
                            .ticketMitigation().toString(),
                    exception);
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
     * @return the SHA-256 checksum of the certificate as a hex string or an empty {@link Optional} when no
     *         configuration exists or the certificate file does not exist
     */
    public Optional<String> getSha256Fingerprint() {
        return getEncodedCertificate() //
                .map(CertificateProvider::sha256) //
                .map(bytes -> bytesToHexWithPadding(bytes, 32));
    }

    private Optional<byte[]> getEncodedCertificate() {
        final Optional<X509Certificate> certificate = getCertificate();
        if (certificate.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(certificate.get().getEncoded());
        } catch (final CertificateEncodingException exception) {
            throw new IllegalStateException(messageBuilder("F-ETC-8")
                    .message("Unable get encoded certificate for {{certificate}}.", certificate.get())
                    .ticketMitigation().toString(), exception);
        }
    }

    static byte[] sha256(final byte[] data) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        } catch (final NoSuchAlgorithmException exception) {
            throw new IllegalStateException(messageBuilder("F-ETC-9")
                    .message("Unable to calculate SHA-256 of certificate content.").ticketMitigation().toString(),
                    exception);
        }
    }

    static String bytesToHexWithPadding(final byte[] bytes, final int byteCount) {
        final String hex = bytesToHex(bytes);
        if (hex.length() >= (byteCount * 2)) {
            return hex;
        }
        return "0".repeat((byteCount * 2) - hex.length()) + hex;
    }

    static String bytesToHex(final byte[] bytes) {
        final String hex = new BigInteger(1, bytes).toString(16);
        if ((hex.length() % 2) == 0) {
            return hex;
        } else {
            return "0" + hex;
        }
    }
}
