package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
            ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE) //
                    .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    @Test
    void testGetDefaultBucket() {
        final Bucket defaultBucket = container.getDefaultBucket();
        assertAll(() -> assertThat(defaultBucket.getBucketFsName(), equalTo(BucketConstants.DEFAULT_BUCKETFS)),
                () -> assertThat(defaultBucket.getBucketName(), equalTo(BucketConstants.DEFAULT_BUCKET)));
    }

    // [itest->dsn~bucket-lists-its-contents~1]
    @Test
    void testListBucketContentsWithRootPath() throws BucketAccessException, InterruptedException {
        assertThat(container.getDefaultBucket().listContents(), hasItem("EXAClusterOS"));
    }

    // [itest->dsn~bucket-lists-its-contents~1]
    @ValueSource(strings = { "EXAClusterOS/", "/EXAClusterOS/" })
    @ParameterizedTest
    void testListBucketContents(final String pathInBucket) throws BucketAccessException, InterruptedException {
        assertThat(container.getDefaultBucket().listContents(pathInBucket), hasItem(startsWith("ScriptLanguages")));
    }

    void testListBucketContentsOfIllegalPathThrowsException() {
        assertThrows(BucketAccessException.class, () -> container.getDefaultBucket().listContents("illegal\\path"));
    }

    // [itest->dsn~uploading-to-bucket~1]
    @Test
    void testUploadFile(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, InterruptedException, TimeoutException {
        final String fileName = "test-uploaded.txt";
        final Path testFile = createTestFile(tempDir, fileName, 10000);
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadFile(testFile, fileName);
        assertThat(bucket.listContents(), hasItem(fileName));
    }

    private Path createTestFile(final Path tempDir, final String fileName, final int sizeInKiB) throws IOException {
        final RandomFileGenerator generator = new RandomFileGenerator();
        final Path path = tempDir.resolve(Path.of(fileName));
        generator.createRandomFile(path, sizeInKiB);
        return path;
    }

    // [itest->dsn~uploading-to-bucket~1]
    @ValueSource(strings = { "dir1/", "dir2/sub2/", "dir3/sub3/subsub3/", "/dir4/", "/dir5/sub5/" })
    @ParameterizedTest
    void testUploadToDirectoryInBucket(final String pathInBucket, @TempDir final Path tempDir)
            throws BucketAccessException, InterruptedException, IOException, TimeoutException {
        final String fileName = "file.txt";
        final Path file = createTestFile(tempDir, fileName, 1);
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadFile(file, pathInBucket);
        assertThat(container.getDefaultBucket().listContents(pathInBucket), contains(fileName));
    }

    // [itest->dsn~uploading-strings-to-bucket~1]
    @Test
    void testUploadStringContent() throws IOException, BucketAccessException, InterruptedException, TimeoutException {
        final String content = "Hello BucketFS!";
        final String pathInBucket = "string-uploaded.txt";
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadStringContent(content, pathInBucket);
        assertThat(bucket.listContents(), hasItem(pathInBucket.toString()));
    }

    @Test
    void testUploadNonExistentFileThrowsException() {
        final Path file = Path.of("/this/path/does/not/exist");
        assertThrows(BucketAccessException.class, () -> container.getDefaultBucket().uploadFile(file, "nowhere.txt"));
    }

    @Test
    void testUploadFileToIllegalUrlThrowsException(@TempDir final Path tempDir) throws IOException {
        final Path file = createTestFile(tempDir, "irrelevant.txt", 1);
        assertThrows(BucketAccessException.class,
                () -> container.getDefaultBucket().uploadFile(file, "this\\is\\an\\illegal\\URL"));
    }

    @Test
    void testUploadContentToIllegalUrlThrowsException() {
        assertThrows(BucketAccessException.class, () -> container.getDefaultBucket()
                .uploadStringContent("irrelevant content", "this\\is\\an\\illegal\\URL"));
    }
}