package com.exasol.containers.ssl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.*;

@Tag("slow")
@Testcontainers
class CertificateProviderIT {

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withReuse(true).withRequiredServices(ExasolService.JDBC);

    @Test
    void testGetCertificate() {
        final Date now = new Date();

        final Optional<X509Certificate> optionalSslCertificate = CONTAINER.getSslCertificate();

        final var sslCertificate = optionalSslCertificate.get();
        assertAll(() -> assertThat(sslCertificate, notNullValue()),
                () -> assertThat(sslCertificate.getIssuerDN().getName(), equalTo("CN=exacluster.local")),
                () -> assertThat(sslCertificate.getSubjectDN().getName(), equalTo("CN=*.exacluster.local")),
                () -> assertThat(sslCertificate.getNotBefore(), lessThan(now)),
                () -> assertThat(sslCertificate.getNotAfter(), greaterThan(now)));
    }

    @Test
    void testGetSha256Fingerprint() {
        final Optional<String> fingerprint = createCertificateProvider().getSha256Fingerprint();

        assertThat(fingerprint.isPresent(), is(true));
        assertThat(fingerprint.get(), matchesPattern("\\w{64}"));
    }

    private CertificateProvider createCertificateProvider() {
        return new CertificateProvider(CONTAINER, new ContainerFileOperations(CONTAINER));
    }
}
