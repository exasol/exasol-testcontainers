package com.exasol.containers.ssl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.junit.jupiter.api.*;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Tag("slow")
@Testcontainers
class CertificateProviderIT {

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withReuse(true);

    @Test
    void testGetCertificate() {
        final Date now = new Date();

        final X509Certificate sslCertificate = CONTAINER.getSslCertificate();

        assertAll(() -> {
            assertThat(sslCertificate, notNullValue());
            assertThat(sslCertificate.getIssuerDN().getName(), equalTo("CN=exacluster.local"));
            assertThat(sslCertificate.getSubjectDN().getName(), equalTo("CN=*.exacluster.local"));
            assertThat(sslCertificate.getNotBefore(), lessThan(now));
            assertThat(sslCertificate.getNotAfter(), greaterThan(now));
        });
    }

    @Test
    @SetSystemProperty(key = "jdk.internal.httpclient.disableHostnameVerification", value = "true")
    void testCertificateUsableWithHttpClientWhenHostnameVerificationDisabled()
            throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
            IOException, InterruptedException, URISyntaxException {

        final SSLContext sslContext = createSslContext();

        final HttpResponse<String> response = sendRequest(sslContext);

        assertThat(response.statusCode(), equalTo(401));
        assertThat(response.body(), equalTo(""));
    }

    @Disabled("Makes other tests fail when executed before")
    @Test
    void testCertificateUsableFailsWhenHostnameVerificationEnabled() throws KeyManagementException, KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException, InterruptedException, URISyntaxException {

        final SSLContext sslContext = createSslContext();

        final IOException exception = assertThrows(IOException.class, () -> sendRequest(sslContext));
        assertThat(exception.getMessage(), equalTo("No subject alternative names present"));
    }

    private HttpResponse<String> sendRequest(final SSLContext sslContext)
            throws IOException, InterruptedException, URISyntaxException {
        final HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build();

        final HttpResponse<String> response = httpClient.send(
                HttpRequest.newBuilder(new URI(CONTAINER.getRpcUrl())).POST(BodyPublishers.ofString("")).build(),
                BodyHandlers.ofString());
        return response;
    }

    private SSLContext createSslContext() throws KeyStoreException, IOException, NoSuchAlgorithmException,
            CertificateException, KeyManagementException {
        final X509Certificate certificate = CONTAINER.getSslCertificate();
        final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        keyStore.setCertificateEntry("caCert", certificate);

        final TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }
}
