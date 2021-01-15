package com.exasol.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class BucketConfigurationTest {
    @Test
    void testGetName() {
        final String expected = "name";
        final BucketConfiguration bucketConfiguration = BucketConfiguration.builder().name(expected).build();
        assertThat(bucketConfiguration.getName(), equalTo(expected));
    }

    @Test
    void testGetReadPassword() {
        final String expected = "read";
        final BucketConfiguration bucketConfiguration = BucketConfiguration.builder().readPassword(expected).build();
        assertThat(bucketConfiguration.getReadPassword(), equalTo(expected));
    }

    @Test
    void testGetWritePassword() {
        final String expected = "write";
        final BucketConfiguration bucketConfiguration = BucketConfiguration.builder().writePassword(expected).build();
        assertThat(bucketConfiguration.getWritePassword(), equalTo(expected));
    }

    @Test
    void testIsPubliclyReadableFalseByDefault() {
        final BucketConfiguration bucketConfiguration = BucketConfiguration.builder().build();
        assertThat(bucketConfiguration.isPubliclyReadable(), equalTo(false));
    }

    @Test
    void testIsPubliclyReadableTrue() {
        final BucketConfiguration bucketConfiguration = BucketConfiguration.builder().publiclyReadable(true).build();
        assertThat(bucketConfiguration.isPubliclyReadable(), equalTo(true));
    }

    @Test
    void testGetBucketFsServiceConfiguration() {
        final BucketFsServiceConfiguration serviceConfiguration = BucketFsServiceConfiguration.builder().name("parent")
                .build();
        final BucketConfiguration bucketConfiguration = BucketConfiguration.builder()
                .bucketFsServiceConfiguration(serviceConfiguration).build();
        assertThat(bucketConfiguration.getBucketFsServiceConfiguration().getName(), equalTo("parent"));
    }
}