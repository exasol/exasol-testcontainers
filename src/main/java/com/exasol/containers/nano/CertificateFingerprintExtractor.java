package com.exasol.containers.nano;

import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.errorreporting.ExaError;

/**
 * Extracts the certificate fingerprint from the Exasol Nano container logs, caching the result for subsequent calls.
 */
class CertificateFingerprintExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateFingerprintExtractor.class);

    /**
     * Matches lines containing the certificate fingerprint, e.g.:
     * 
     * <pre>
     * [INFO] "exaplus -u sys -P exasol -c localhost/f6cd8790e70f8a47ff9f606dbbfa48a3b8c6a92c2f1f89e2d27042970a46cff2:8563"
     * </pre>
     */
    private static final Pattern FINGERPRINT_PATTERN = Pattern.compile("/([0-9a-fA-F]{64}):\\d+");

    private String certificateFingerprint;

    private final Supplier<String> logSupplier;

    public CertificateFingerprintExtractor(final Supplier<String> logSupplier) {
        this.logSupplier = logSupplier;
    }

    String getCertificateFingerprint() {
        if (this.certificateFingerprint == null) {
            this.certificateFingerprint = extractFingerprintFromLogs();
        }
        return this.certificateFingerprint;
    }

    private String extractFingerprintFromLogs() {
        final String logs = this.logSupplier.get();
        final var matcher = FINGERPRINT_PATTERN.matcher(logs);
        if (matcher.find()) {
            final String fingerprint = matcher.group(1);
            LOGGER.info("Extracted certificate fingerprint: {}", fingerprint);
            return fingerprint;
        } else {
            throw new IllegalStateException(ExaError.messageBuilder("E-ETC-49")
                    .message("Failed to extract certificate fingerprint from Exasol Nano logs: {{complete log}}.", logs)
                    .ticketMitigation()
                    .toString());
        }
    }
}
