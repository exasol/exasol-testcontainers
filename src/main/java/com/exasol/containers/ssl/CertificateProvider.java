package com.exasol.containers.ssl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.cert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.containers.ContainerFileOperations;
import com.exasol.containers.ExasolContainer;

public class CertificateProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateProvider.class);

    private final ExasolContainer<? extends ExasolContainer<?>> container;
    private final ContainerFileOperations fileOperations;

    public CertificateProvider(final ExasolContainer<? extends ExasolContainer<?>> container,
            final ContainerFileOperations fileOperations) {
        this.container = container;
        this.fileOperations = fileOperations;
    }

    public X509Certificate getCertificate() {
        final String certPath = this.container.getClusterConfiguration().getSslCertificatePath();
        final String certContent = this.fileOperations.readFile(certPath, StandardCharsets.UTF_8);
        LOGGER.debug("Read certificate from file {} contains {} chars", certPath, certContent.length());
        final X509Certificate certificate = parseCertificate(certContent);
        logCertificate(certificate);
        return certificate;
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

    private X509Certificate parseCertificate(final String certContent) {
        try (final InputStream is = new ByteArrayInputStream(certContent.getBytes(StandardCharsets.UTF_8))) {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(is);
        } catch (final CertificateException | IOException e) {
            throw new IllegalStateException("Error parsing certificate '" + certContent + "'", e);
        }
    }
}
