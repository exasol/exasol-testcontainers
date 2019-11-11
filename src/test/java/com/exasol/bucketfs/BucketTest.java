package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolContainerConstants;

@Testcontainers
class BucketTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BucketTest.class);

    @Container
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>(
            ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE).withLogConsumer(new Slf4jLogConsumer(LOGGER));

    @Test
    void testGetDefaultBucket() {
        final Bucket defaultBucket = container.getDefaultBucket();
        assertAll(() -> assertThat(defaultBucket.getBucketFsName(), equalTo(BucketConstants.DEFAULT_BUCKETFS)),
                () -> assertThat(defaultBucket.getBucketName(), equalTo(BucketConstants.DEFAULT_BUCKET)));
    }

    @Test
    void testListBucketContents() throws BucketAccessException, InterruptedException {
        assertThat(container.getDefaultBucket().listContents("/"), hasItem(startsWith("EXAClusterOS")));
    }

    @Test
    void testUploadFile(@TempDir final Path tempDir) throws IOException, BucketAccessException, InterruptedException {
        final String pathInBucket = "test-uploaded.txt";
        final Path testFile = Files.writeString(tempDir.resolve("test.txt"), "content", StandardOpenOption.CREATE);
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadFile(testFile, pathInBucket);
        assertThat(bucket.listContents("/"), hasItem(pathInBucket));
    }

    @Test
    void testUploadStringContent() throws IOException, BucketAccessException, InterruptedException {
        final String content = "Hello BucketFS!";
        final String pathInBucket = "string-uploaded.txt";
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadStringContent(content, pathInBucket);
        assertThat(bucket.listContents("/"), hasItem(pathInBucket));
    }
}