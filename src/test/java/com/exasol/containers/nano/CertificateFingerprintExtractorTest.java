package com.exasol.containers.nano;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.exasol.testutil.ExceptionAssertions;

class CertificateFingerprintExtractorTest {
    private static final String FINGERPRINT = "f6cd8790e70f8a47ff9f606dbbfa48a3b8c6a92c2f1f89e2d27042970a46cff2";
    private static final String LOG_LINE = "[INFO]   \"exaplus -u sys -P exasol -c localhost/" + FINGERPRINT
            + ":8563\"";

    @Test
    void extractsCertificateFingerprintFromLogs() {
        final CertificateFingerprintExtractor extractor = new CertificateFingerprintExtractor(() -> LOG_LINE);

        assertThat(extractor.getCertificateFingerprint(), equalTo(FINGERPRINT));
    }

    @Test
    void returnsCachedCertificateFingerprint() {
        final AtomicInteger supplierCalls = new AtomicInteger();
        final CertificateFingerprintExtractor extractor = new CertificateFingerprintExtractor(() -> {
            supplierCalls.incrementAndGet();
            return LOG_LINE;
        });

        assertThat(extractor.getCertificateFingerprint(), equalTo(FINGERPRINT));
        assertThat(extractor.getCertificateFingerprint(), equalTo(FINGERPRINT));
        assertThat(supplierCalls.get(), equalTo(1));
    }

    @Test
    void failsWhenCertificateFingerprintIsMissingAndIncludesCompleteLog() {
        final String logs = "startup line\nDatabase is now up and running\n";
        final CertificateFingerprintExtractor extractor = new CertificateFingerprintExtractor(() -> logs);

        ExceptionAssertions.assertThrowsWithMessage(IllegalStateException.class, extractor::getCertificateFingerprint,
                allOf(containsString("Failed to extract certificate fingerprint from Exasol Nano logs"),
                        containsString(logs)));
    }
}
