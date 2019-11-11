package com.exasol.exaconf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

import com.exasol.config.*;

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

    private ClusterConfiguration parseConfiguration(final String rawConfig) {
        return new ConfigurationParser(rawConfig).parse();
    }

    @Test
    void testGetBucketConfiguration() {
        final ClusterConfiguration clusterConfiguration = parseConfiguration("[BucketFS : the-fs]\n" //
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
}