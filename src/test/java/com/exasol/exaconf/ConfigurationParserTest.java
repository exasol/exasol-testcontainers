package com.exasol.exaconf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.config.*;

@Tag("fast")
class ConfigurationParserTest {
    @Test
    void testGetBucketFsServiceConfiguration() {
        final ClusterConfiguration clusterConfiguration = parseConfiguration("[BucketFS : the-fs]\n" //
                + "    HttpPort = 6583\n" //
                + "    HttpsPort = 7878");
        final BucketFsServiceConfiguration serviceConfiguration = clusterConfiguration
                .getBucketFsServiceConfiguration("the-fs");
        assertAll(() -> assertThat(serviceConfiguration.getName(), equalTo("the-fs")),
                () -> assertThat(serviceConfiguration.getHttpPort(), equalTo(6583)),
                () -> assertThat(serviceConfiguration.getHttpsPort(), equalTo(7878)));
    }

    @Test
    void testGetBucketFsServiceConfigurationMissingHttpPort() {
        final ClusterConfiguration clusterConfiguration = parseConfiguration("[BucketFS : the-fs]\n" //
                + "    HttpsPort = 7878");
        final BucketFsServiceConfiguration serviceConfiguration = clusterConfiguration
                .getBucketFsServiceConfiguration("the-fs");
        assertAll(() -> assertThat(serviceConfiguration.getName(), equalTo("the-fs")),
                () -> assertThat(serviceConfiguration.getHttpPort(), equalTo(0)),
                () -> assertThat(serviceConfiguration.getHttpsPort(), equalTo(7878)));
    }

    private ClusterConfiguration parseConfiguration(final String rawConfig) {
        return new ConfigurationParser(rawConfig).parse();
    }

    @Test
    void testGetBucketConfiguration() {
        final ClusterConfiguration clusterConfiguration = parseConfiguration("[BucketFS : the-fs]\n" //
                + "    HttpPort = 6583\n" //
                + "    [[Bucket : the-bucket]]\n" //
                + "    # Ignore this comment when parsing.\n" //
                + "        ReadPasswd = cmVhZA==\n" //
                + "        WritePasswd = d3JpdGU=\n" //
                + "        Public = True");
        final BucketConfiguration bucketConfiguration = clusterConfiguration //
                .getBucketFsServiceConfiguration("the-fs") //
                .getBucketConfiguration("the-bucket");
        assertAll(() -> assertThat(bucketConfiguration.getName(), equalTo("the-bucket")),
                () -> assertThat(bucketConfiguration.getReadPassword(), equalTo("read")),
                () -> assertThat(bucketConfiguration.getWritePassword(), equalTo("write")),
                () -> assertThat(bucketConfiguration.isPubliclyReadable(), equalTo(true)));
    }

    @Test
    void testGetDefaultBucketPasswords() {
        final ClusterConfiguration config = parseConfiguration("[BucketFS : bfsdefault]\n" //
                + "    [[Bucket : default]]\n" //
                + "    # Ignore this comment when parsing.\n" //
                + "        ReadPasswd = cmVhZA==\n" //
                + "        WritePasswd = d3JpdGU=");
        assertAll(() -> assertThat(config.getDefaultBucketReadPassword(), equalTo("read")),
                () -> assertThat(config.getDefaultBucketWritePassword(), equalTo("write")));
    }

    @Test
    void testGetDefaultBucketPasswordNotFoundInOtherBucket() {
        final String rawConfig = "[BucketFS : bfsdefault]\n" //
                + "    [[Bucket : other]]\n" //
                + "        ReadPasswd = cmVhZA==\n" //
                + "        WritePasswd = d3JpdGU=";
        final ClusterConfiguration config = parseConfiguration(rawConfig);
        assertAll(() -> assertThat(config.getDefaultBucketReadPassword(), nullValue()),
                () -> assertThat(config.getDefaultBucketWritePassword(), nullValue()));
    }

    @Test
    void testGetAuthenticationToken() {
        final ClusterConfiguration config = parseConfiguration("[Global]\n" //
                + "    AuthenticationToken = AuthToken\n");
        assertThat(config.getAuthenticationToken(), equalTo("AuthToken"));
    }

    @Test
    void testGetTlsCertificatePath() {
        final ClusterConfiguration config = parseConfiguration("[SSL]\n" //
                + "    Cert = /path/to/ssl.crt\n");
        assertThat(config.getTlsCertificatePath(), equalTo("/path/to/ssl.crt"));
    }

    // The following situations should not occur, but we want to be fault-tolerant here.
    @ValueSource(strings = { "    Ignore this illegal line.", "Ignore this illegal line.",
            "    Ignore #this illegal line.", "=valueWithoutKey", "     " })
    @ParameterizedTest
    void testIgnoreLineWithoutCommentAndAssignment(final String illegalLine) {
        final ClusterConfiguration clusterConfiguration = parseConfiguration("[BucketFS : the-fs]\n" //
                + "    HttpsPort = 1234\n" //
                + "    [[Bucket : the-bucket]]\n" //
                + illegalLine + "\n"//
                + "        Public = True");
        final BucketConfiguration bucketConfiguration = clusterConfiguration //
                .getBucketFsServiceConfiguration("the-fs") //
                .getBucketConfiguration("the-bucket");
        assertThat(bucketConfiguration.isPubliclyReadable(), equalTo(true));
    }

    @Test
    void testGetTimeZone() {
        final ClusterConfiguration clusterConfiguration = parseConfiguration("[Global]\nTimezone = Asia/Taipei");
        assertThat(clusterConfiguration.getTimeZone().toString(), containsString("Asia/Taipei"));
    }

    @Test
    void testGetDatabaseServiceConfiguration() {
        final ClusterConfiguration clusterConfiguration = parseConfiguration("[DB : DB1]\nPort = 3456");
        final DatabaseServiceConfiguration databaseServiceConfiguration = clusterConfiguration
                .getDefaultDatabaseServiceConfiguration();
        assertAll(() -> assertThat(databaseServiceConfiguration.getDatabaseName(), equalTo("DB1")),
                () -> assertThat(databaseServiceConfiguration.getPort(), equalTo(3456)));
    }

    @Test
    void testGetDatabaseNames() {
        final ClusterConfiguration clusterConfiguration = parseConfiguration(
                "[DB : Fred]\nPort=1\nNodes=11\n[DB : Wilma]\nPort=2\n[DB : Barney]\nPort=3");
        assertThat(clusterConfiguration.getDatabaseNames(), contains("Fred", "Wilma", "Barney"));
    }

    @Test
    void testGetBucketFsServiceNames() {
        final ClusterConfiguration clusterConfiguration = parseConfiguration(
                "[BucketFS : Fred]\nHttpPort=1\n[[Bucket : default]]\n[BucketFS : Wilma]\nHttpPort=2\n[BucketFS : Barney]\nHttpPort=3");
        assertThat(clusterConfiguration.getBucketFsServiceNames(), contains("Fred", "Wilma", "Barney"));
    }

    @Test
    void testParseConfigurationWithRootPasswordSet() {
        assertDoesNotThrow(() -> parseConfiguration("    [[root]]\n" + "        ID = 0\n" + "        Group = root\n"
                + "        LoginEnabled = True\n"
                + "        AdditionalGroups = exausers, exadbadm, exastoradm, exabfsadm, exaadm\n"
                + "        Passwd = $6$ypRK9ia5lkj9/DWF$SxzzizsUp4AZYmk2m2PgKeA8fDT4Ou3FreNQkPFBoTbaR4HI0gNs0o4lEwtAAManFUeU00iq8c/mfAWmtyLQI/\n"));
    }
}
