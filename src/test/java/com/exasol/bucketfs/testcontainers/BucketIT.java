package com.exasol.bucketfs.testcontainers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.bucketfs.*;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor.FilterStrategy;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.exec.ExitCode;

@Tag("slow")
@Testcontainers
// [itest->dsn~bucket-api~1]
class BucketIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(BucketIT.class);

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>() //
            .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    @Test
    void testGetDefaultBucket() {
        final Bucket defaultBucket = container.getDefaultBucket();
        assertAll(() -> assertThat(defaultBucket.getBucketFsName(), equalTo(BucketConstants.DEFAULT_BUCKETFS)),
                () -> assertThat(defaultBucket.getBucketName(), equalTo(BucketConstants.DEFAULT_BUCKET)));
    }

    void testListBucketContentsOfIllegalPathThrowsException() {
        assertThrows(BucketAccessException.class, () -> container.getDefaultBucket().listContents("illegal\\path"));
    }

    @Test
    void testUploadFile(@TempDir final Path tempDir) throws IOException, BucketAccessException, TimeoutException {
        final String fileName = "test-uploaded.txt";
        final Path testFile = createTestFile(tempDir, fileName, 10000);
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadFile(testFile, fileName);
        assertThat(bucket.listContents(), hasItem(fileName));
    }

    @Test
    void uploadFileWithFilterStrategyLineNumber(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, TimeoutException {
        final String fileName = "test-uploaded.txt";
        final Path testFile = createTestFile(tempDir, fileName, 10000);
        final Bucket bucket = container.getBucket( //
                BucketConstants.DEFAULT_BUCKETFS, //
                BucketConstants.DEFAULT_BUCKET, //
                FilterStrategy.LINE_NUMBER);
        bucket.uploadFile(testFile, fileName);
        assertThat(bucket.listContents(), hasItem(fileName));
    }

    @Test
    void testUploadZipArchive() throws BucketAccessException, TimeoutException {
        final String filename = "sample-archive.zip";
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadInputStream(() -> BucketIT.class.getResourceAsStream("/" + filename), filename);
        assertThat(bucket.listContents(), hasItem(filename));
    }

    private Path createTestFile(final Path tempDir, final String fileName, final int sizeInKiB) throws IOException {
        final RandomFileGenerator generator = new RandomFileGenerator();
        final Path path = tempDir.resolve(Path.of(fileName));
        generator.createRandomFile(path, sizeInKiB);
        return path;
    }

    @ValueSource(strings = { "dir1/", "dir2/sub2/", "dir3/sub3/subsub3/", "/dir4/", "/dir5/sub5/" })
    @ParameterizedTest
    void testUploadToDirectoryInBucket(final String pathInBucket, @TempDir final Path tempDir)
            throws BucketAccessException, IOException, TimeoutException {
        final String fileName = "file.txt";
        final Path file = createTestFile(tempDir, fileName, 1);
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadFile(file, pathInBucket);
        assertThat(container.getDefaultBucket().listContents(pathInBucket), contains(fileName));
    }

    @Test
    void testUploadStringContent() throws BucketAccessException, InterruptedException, TimeoutException {
        final String content = "Hello BucketFS!";
        final String pathInBucket = "string-uploaded.txt";
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadStringContent(content, pathInBucket);
        assertThat(bucket.listContents(), hasItem(pathInBucket.toString()));
    }

    @Test
    void testUploadInputStreamContent() throws BucketAccessException, TimeoutException {
        final String content = "Hello BucketFS!";
        final String pathInBucket = "string-uploaded.txt";
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadInputStream(() -> new ByteArrayInputStream(content.getBytes()), pathInBucket);
        assertThat(bucket.listContents(), hasItem(pathInBucket));
    }

    @Test
    void testUploadNonExistentFileThrowsException() {
        final Path file = Path.of("/this/path/does/not/exist");
        assertThrows(FileNotFoundException.class, () -> container.getDefaultBucket().uploadFile(file, "nowhere.txt"));
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

    @Test
    void testDownloadFile(@TempDir final Path tempDir)
            throws InterruptedException, BucketAccessException, TimeoutException, IOException {
        final String fileName = "read_me.txt";
        final Bucket bucket = container.getDefaultBucket();
        final String content = "read me";
        bucket.uploadStringContent(content, fileName);
        final Path pathToFile = tempDir.resolve(fileName);
        bucket.downloadFile(fileName, pathToFile);
        assertThat(Files.readString(pathToFile), equalTo(content));
    }

    @Test
    void testDownloadFileThrowsExceptionOnIllegalPathInBucket(@TempDir final Path tempDir) {
        final Path pathToFile = tempDir.resolve("irrelevant");
        final String pathInBucket = "this/path/does/not/exist";
        final Bucket bucket = container.getDefaultBucket();
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> bucket.downloadFile(pathInBucket, pathToFile));
        assertThat(exception.getMessage(), startsWith("E-BFSJ-2: File or directory not found trying to download "));
    }

    @Test
    void testDownloadFileThrowsExceptionOnIllegalLocalPath(@TempDir final Path tempDir)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final Path pathToFile = tempDir.resolve("/this/path/does/not/exist");
        final String pathInBucket = "foo.txt";
        final Bucket bucket = container.getDefaultBucket();
        bucket.uploadStringContent("some content", pathInBucket);
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> bucket.downloadFile(pathInBucket, pathToFile));
        assertThat(exception.getCause(), instanceOf(IOException.class));
    }

    @Test
    void testReplaceFile(@TempDir final Path tempDir)
            throws InterruptedException, BucketAccessException, TimeoutException, IOException {
        final int scaleContentSizeBy = 10000000;
        final String fileName = "replace_me.txt";
        final String absolutePathInContainer = "/exa/data/bucketfs/bfsdefault/.dest/default/" + fileName;
        final String contentA = "0123456789\n";
        final Path fileA = Files.writeString(tempDir.resolve("a.txt"), contentA.repeat(scaleContentSizeBy));
        final String contentB = "abcdeABCDE\n";
        final Path fileB = Files.writeString(tempDir.resolve("b.txt"), contentB.repeat(scaleContentSizeBy));
        final Bucket bucket = container.getDefaultBucket();
        for (int i = 1; i <= 10; ++i) {
            final boolean useA = (i % 2) == 1;
            final Path currentFile = useA ? fileA : fileB;
            final String currentFirstLine = useA ? contentA : contentB;
            bucket.uploadFile(currentFile, fileName);
            final ExecResult execInContainer = container.execInContainer("head", "-n", "1", absolutePathInContainer);
            if (execInContainer.getExitCode() == ExitCode.OK) {
                assertThat("Upload number " + i + ": file " + (useA ? "A" : "B"), execInContainer.getStdout(),
                        equalTo(currentFirstLine));
            } else {
                fail("Unable to read hash from file in container");
            }
        }
    }
}
