package com.exasol.containers.nano;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.OutputFrame;

import com.exasol.errorreporting.ExaError;

/**
 * Extracts the certificate fingerprint from the Exasol Nano container logs
 * and provides a method to wait for it to become available.
 */
class CertificateFingerprintExtractor implements Consumer<OutputFrame> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateFingerprintExtractor.class);

    /**
     * Matches lines containing the certificate fingerprint, e.g.:
     * 
     * <pre>
     * [INFO] "exaplus -u sys -P exasol -c localhost/f6cd8790e70f8a47ff9f606dbbfa48a3b8c6a92c2f1f89e2d27042970a46cff2:8563"
     * </pre>
     */
    private static final Pattern FINGERPRINT_PATTERN = Pattern.compile("/([0-9a-fA-F]{64}):\\d+");

    private final StringBuilder log = new StringBuilder();
    private volatile String certificateFingerprint;

    @Override
    public synchronized void accept(final OutputFrame frame) {
        final String line = frame.getUtf8String();
        this.log.append(line);
        if (this.certificateFingerprint != null) {
            return;
        }
        final Matcher matcher = FINGERPRINT_PATTERN.matcher(line);
        if (matcher.find()) {
            this.certificateFingerprint = matcher.group(1);
        }
    }

    synchronized String getCertificateFingerprint() {
        if (this.certificateFingerprint == null) {
            throw new IllegalStateException(ExaError.messageBuilder("E-ETC-47")
                    .message("Certificate fingerprint is not available. Complete log: {{log}}",
                            this.log.toString())
                    .ticketMitigation()
                    .toString());
        }
        return this.certificateFingerprint;
    }

    void waitForCertificateFingerprint(final Duration timeout) {
        LOGGER.debug("Waiting {} for certificate fingerprint to become available", timeout);
        if (this.certificateFingerprint != null) {
            return;
        }
        final long endTime = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < endTime) {
            if (this.certificateFingerprint != null) {
                return;
            }
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for certificate fingerprint", e);
            }
        }
        throw new IllegalStateException(ExaError.messageBuilder("E-ETC-48")
                .message("Timed out after {{timeout}} waiting for certificate fingerprint. Complete log: {{log}}",
                        timeout, this.log.toString())
                .ticketMitigation()
                .toString());
    }
}
