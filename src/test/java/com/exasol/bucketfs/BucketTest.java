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

    // [itest->dsn~bucket-lists-its-contents~1]
    @Test
    void testListBucketContents() throws BucketAccessException, InterruptedException {
        assertThat(container.getDefaultBucket().listContents(), hasItem(startsWith("EXAClusterOS")));
    }

    // [itest->dsn~bucket-lists-its-contents~1]
    @Test
    void testListBucketContentsWithRootPath() throws BucketAccessException, InterruptedException {
        assertThat(container.getDefaultBucket().listContents(), hasItem(startsWith("EXAClusterOS")));
    }

    // [itest->dsn~uploading-to-bucket~1]
    @Test
    void testUploadFile(@TempDir final Path tempDir) throws IOException, BucketAccessException, InterruptedException {
        final String fileName = "test-uploaded.txt";
        final Path testFile = createTestFile(tempDir, fileName);
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadFile(testFile, fileName);
        assertThat(bucket.listContents(), hasItem(fileName));
    }

    private Path createTestFile(final Path tempDir, final String fileName) throws IOException {
        return Files.writeString(tempDir.resolve(fileName), "content", StandardOpenOption.CREATE);
    }

    // [itest->dsn~uploading-to-bucket~1]
    @Test
    void testUploadToDirectoryInBucket(@TempDir final Path tempDir)
            throws BucketAccessException, InterruptedException, IOException {
        final String fileName = "file.txt";
        final String pathInBucket = "directory/";
        final Path testFile = createTestFile(tempDir, fileName);
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadFile(testFile, pathInBucket);
        assertThat(container.getDefaultBucket().listContents(pathInBucket), contains(fileName));
    }

    // [itest->dsn~uploading-strings-to-bucket~1]
    @Test
    void testUploadStringContent() throws IOException, BucketAccessException, InterruptedException {
        final String content = "Hello BucketFS!";
        final String pathInBucket = "string-uploaded.txt";
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadStringContent(content, pathInBucket);
        assertThat(bucket.listContents(), hasItem(pathInBucket.toString()));
    }
}