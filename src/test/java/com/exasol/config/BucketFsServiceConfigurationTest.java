package com.exasol.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class BucketFsServiceConfigurationTest {
    @Test
    void testGetName() {
        final String expected = "the-fs";
        final BucketFsServiceConfiguration serviceConfiguration = BucketFsServiceConfiguration.builder().name(expected)
                .build();
        assertThat(serviceConfiguration.getName(), equalTo(expected));
    }

    @Test
    void testHttpPort() {
        final int expected = 1111;
        final BucketFsServiceConfiguration bucketConfiguration = BucketFsServiceConfiguration.builder()
                .httpPort(expected).build();
        assertThat(bucketConfiguration.getHttpPort(), equalTo(expected));
    }

    @Test
    void testGetHttpsPort() {
        final int expected = 2222;
        final BucketFsServiceConfiguration bucketConfiguration = BucketFsServiceConfiguration.builder()
                .httpsPort(expected).build();
        assertThat(bucketConfiguration.getHttpsPort(), equalTo(expected));
    }

    @Test
    void testGetBucketConfiguration() {
        final String bucketName = "the-bucket";
        final BucketConfiguration bucketConfiguration = BucketConfiguration.builder().name(bucketName).build();
        final BucketFsServiceConfiguration serviceConfiguration = BucketFsServiceConfiguration.builder()
                .addBucketConfiguration(bucketConfiguration).build();
        assertThat(serviceConfiguration.getBucketConfiguration(bucketName).getName(), equalTo(bucketName));
    }

    @Test
    void testGetBucketConfigurationThrowsExceptionIfBucketDoesNotExist() {
        final BucketFsServiceConfiguration serviceConfiguration = BucketFsServiceConfiguration.builder().build();
        assertThrows(IllegalArgumentException.class, () -> serviceConfiguration.getBucketConfiguration("non-existent"));
    }
}