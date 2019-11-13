package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.exasol.config.*;

class BucketFactoryTest {
    // [utest->dsn~bucket-factory-injects-access-credentials~1]
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
        final BucketFactory factory = new BucketFactory(ipAddress, clusterConfigurationMock, portMappings);
        final Bucket bucket = factory.getBucket(serviceName, bucketName);
        assertAll(() -> assertThat(bucket.getReadPassword(), equalTo(readPassword)),
                () -> assertThat(bucket.getWritePassword(), equalTo(writePassword)));
    }
}