package com.exasol.containers.nano;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.OutputFrame.OutputType;

import com.exasol.testutil.ExceptionAssertions;

class CertificateFingerprintExtractorTest {
    private static final String FINGERPRINT = "f6cd8790e70f8a47ff9f606dbbfa48a3b8c6a92c2f1f89e2d27042970a46cff2";

    private final CertificateFingerprintExtractor extractor = new CertificateFingerprintExtractor();

    @Test
    void extractsCertificateFingerprintFromNanoLogLine() {
        this.extractor.accept(frame("[INFO]   \"exaplus -u sys -P exasol -c localhost/" + FINGERPRINT + ":8563\""));

        assertThat(this.extractor.getCertificateFingerprint(), equalTo(FINGERPRINT));
    }

    @Test
    void keepsFirstExtractedCertificateFingerprint() {
        this.extractor.accept(frame("[INFO]   \"exaplus -u sys -P exasol -c localhost/" + FINGERPRINT + ":8563\""));
        this.extractor.accept(frame("[INFO]   \"exaplus -u sys -P exasol -c localhost/"
                + "1234567890123456789012345678901234567890123456789012345678901234:8563\""));

        assertThat(this.extractor.getCertificateFingerprint(), equalTo(FINGERPRINT));
    }

    @Test
    void getCertificateFingerprintFailsWhenUnavailableAndIncludesCompleteLog() {
        this.extractor.accept(frame("first log line\n"));
        this.extractor.accept(frame("second log line\n"));

        ExceptionAssertions.assertThrowsWithMessage(IllegalStateException.class,
                this.extractor::getCertificateFingerprint,
                allOf(containsString("Certificate fingerprint is not available"),
                        containsString("first log line\nsecond log line\n")));
    }

    @Test
    void waitForCertificateFingerprintReturnsWhenFingerprintIsAvailable() {
        this.extractor.accept(frame("[INFO]   \"exaplus -u sys -P exasol -c localhost/" + FINGERPRINT + ":8563\""));

        assertDoesNotThrow(() -> this.extractor.waitForCertificateFingerprint(Duration.ZERO));
    }

    @Test
    void waitForCertificateFingerprintFailsOnTimeoutAndIncludesCompleteLog() {
        this.extractor.accept(frame("startup log\n"));

        ExceptionAssertions.assertThrowsWithMessage(IllegalStateException.class,
                () -> this.extractor.waitForCertificateFingerprint(Duration.ZERO),
                allOf(containsString("Timed out after PT0S waiting for certificate fingerprint"),
                        containsString("startup log\n")));
    }

    private static OutputFrame frame(final String content) {
        return new OutputFrame(OutputType.STDOUT, content.getBytes(StandardCharsets.UTF_8));
    }
}
