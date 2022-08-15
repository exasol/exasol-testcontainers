package com.exasol.bucketfs.testcontainers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.Bucket;
import com.exasol.config.*;

@Tag("fast")
class TestcontainerBucketFactoryTest {
    // [utest->dsn~bucket-api~1]
    @Test
    void testGetBucketInjectsAccessCredentials() {
        final String readPassword = "foo";
        final String writePassword = "bar";
        final String serviceName = "the_service";
        final String bucketName = "the_bucket";
        final String ipAddress = "192.168.1.1";
        final int port = 2850;
        final Map<Integer, Integer> portMappings = Map.of(port, port);
        final ClusterConfiguration clusterConfigurationMock = mock(ClusterConfiguration.class);
        final BucketConfiguration bucketConfiguration = BucketConfiguration.builder().name(bucketName)
                .readPassword(readPassword).writePassword(writePassword).build();
        final BucketFsServiceConfiguration serviceConfiguration = BucketFsServiceConfiguration.builder()
                .name(serviceName).httpPort(port).addBucketConfiguration(bucketConfiguration).build();
        when(clusterConfigurationMock.getBucketFsServiceConfiguration(any())).thenReturn(serviceConfiguration);
        final TestcontainerBucketFactory factory = TestcontainerBucketFactory.builder() //
                .host(ipAddress) //
                .clusterConfiguration(clusterConfigurationMock) //
                .portMappings(portMappings) //
                .build();
        final Bucket bucket = factory.getBucket(serviceName, bucketName);
        assertAll(() -> assertThat(bucket.getReadPassword(), equalTo(readPassword)),
                () -> assertThat(bucket.getWritePassword(), equalTo(writePassword)));
    }
}