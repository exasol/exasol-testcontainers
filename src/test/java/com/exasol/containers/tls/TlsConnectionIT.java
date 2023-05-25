package com.exasol.containers.tls;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.*;
import java.security.cert.*;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;

import javax.net.ssl.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.*;
import com.exasol.testutil.ExceptionAssertions;

@Tag("slow")
@Testcontainers
class TlsConnectionIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withReuse(true).withRequiredServices(ExasolService.JDBC);

    // Skip TLS tests for version 8 until https://github.com/exasol/exasol-testcontainers/issues/232 is solved.
    @BeforeAll
    static void beforeAll() {
        Assumptions.assumeFalse(CONTAINER.getDockerImageReference().getMajor() == 8,
                "TLS with version 8 not yet supported. See https://github.com/exasol/exasol-testcontainers/issues/232");
    }

    @Test
    void testJdbcConnectionWithCertificate()
            throws SQLException {
        final String fingerprint = getFingerprint();
        final String url = "jdbc:exa:" + CONTAINER.getHost() + "/" + fingerprint + ":"
                + CONTAINER.getFirstMappedDatabasePort() + ";validateservercertificate=1";

        final Driver driver = CONTAINER.getJdbcDriverInstance();
        final Properties info = new Properties();
        info.put("user", CONTAINER.getUsername());
        info.put("password", CONTAINER.getPassword());
        try (Connection connection = driver.connect(url, info)) {
            final PreparedStatement statement = connection.prepareStatement("select 1 from dual");
            assertTrue(statement.execute());
            assertTrue(statement.getResultSet().next());
        }
    }

    @Test
    void testGetTlsCertificateFingerprint() {
        final Optional<String> actualFingerprint = CONTAINER.getTlsCertificateFingerprint();
        final String expectedFingerprint = getFingerprint();
        if (actualFingerprint.isPresent()) {
            final String actual = actualFingerprint.get();
            assertAll(() -> assertThat(actual, not(emptyOrNullString())), () -> assertThat(actual, hasLength(64)),
                    () -> assertThat(actual, equalTo(expectedFingerprint)));
        } else {
            fail("Unable to get actual TLS fingerprint");
        }
    }

    private String getFingerprint() {
        final Optional<String> fingerprint = new CertificateProvider(CONTAINER, new ContainerFileOperations(CONTAINER))
                .getSha256Fingerprint();
        if (fingerprint.isPresent())
            return fingerprint.get();
        else
            throw new IllegalStateException("Unable to retrieve TLS fingerprint from certificate provider");
    }

    @Test
    @Disabled("Requires manual starting, must run as a single test")
    void testCertificateUsableWithHttpClientWhenHostnameVerificationDisabled() throws Throwable {
        final SSLContext sslContext = createSslContextWithCertificate();
        runWithSystemProperty("jdk.internal.httpclient.disableHostnameVerification", "true", () -> {
            final HttpResponse<String> response = sendRequestWithHttpClient(sslContext);
            assertThat(response.statusCode(), equalTo(401));
            assertThat(response.body(), equalTo(""));
        });
    }

    private void runWithSystemProperty(final String key, final String value, final Executable executable)
            throws Throwable {
        final String originalValue = System.getProperty(key);
        try {
            System.setProperty(key, value);
            executable.execute();
        } finally {
            if (originalValue == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, originalValue);
            }
        }
    }

    @Test
    void testJdbcUrlContainsFingerprint() {
        final String jdbcUrl = CONTAINER.getJdbcUrl();
        final String expectedFingerprint = getFingerprint();
        assertThat(jdbcUrl, containsString(";fingerprint=" + expectedFingerprint + ";"));
    }

    @Test
    void testHttpsUrlConnectionFailsWithoutSslCertificate() throws IOException {
        final HttpsURLConnection connection = prepareHttpsURLConnection(null, null);
        ExceptionAssertions.assertThrowsWithMessage(SSLHandshakeException.class, connection::getResponseCode,
                "PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target");
    }

    @Test
    void testHttpsUrlConnectionFailsWithoutHostnameVerifier() throws IOException, KeyManagementException,
            KeyStoreException, NoSuchAlgorithmException, CertificateException {
        final SSLContext sslContext = createSslContextWithCertificate();

        final HttpsURLConnection connection = prepareHttpsURLConnection(sslContext, null);

        ExceptionAssertions.assertThrowsWithMessage(SSLHandshakeException.class, connection::getResponseCode,
                either(equalTo("No subject alternative names present"))
                        .or(equalTo("No name matching localhost found")));
    }

    @Test
    void testHttpsUrlConnectionSucceedsWithCertificateAndHostnameVerifier() throws IOException, KeyManagementException,
            KeyStoreException, NoSuchAlgorithmException, CertificateException {
        final SSLContext sslContext = createSslContextWithCertificate();
        final HostnameVerifier hostnameVerifier = (hostname, session) -> true;
        final HttpsURLConnection connection = prepareHttpsURLConnection(sslContext, hostnameVerifier);
        assertThat(connection.getResponseCode(), either(equalTo(401)).or(equalTo(404)).or(equalTo(405)));
    }

    private HttpsURLConnection prepareHttpsURLConnection(final SSLContext sslContext,
            final HostnameVerifier hostnameVerifier) throws IOException {
        final URL url = new URL(CONTAINER.getRpcUrl());
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        if (sslContext != null) {
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
        }
        if (hostnameVerifier != null) {
            connection.setHostnameVerifier(hostnameVerifier);
        }
        connection.setRequestMethod("POST");
        return connection;
    }

    private SSLContext createSslContextWithCertificate() throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException, KeyManagementException {
        final Optional<X509Certificate> tlsCertificate = CONTAINER.getTlsCertificate();
        if (tlsCertificate.isPresent()) {
            final X509Certificate certificate = tlsCertificate.get();
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("caCert", certificate);
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            return sslContext;
        } else {
            throw new IllegalStateException("Unable to retrieve TLS certificate trying to create SSL context.");
        }
    }

    private HttpResponse<String> sendRequestWithHttpClient(final SSLContext sslContext)
            throws IOException, InterruptedException, URISyntaxException {
        final HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build();
        final HttpRequest request = HttpRequest.newBuilder(new URI(CONTAINER.getRpcUrl()))
                .POST(BodyPublishers.ofString("")).build();
        return httpClient.send(request, BodyHandlers.ofString());
    }
}
