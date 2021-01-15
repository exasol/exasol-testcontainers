package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.*;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Tag("slow")
@Testcontainers
class BucketContentSyncIT {
    private static RandomFileGenerator GENERATOR = new RandomFileGenerator();
    @Container
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>();

    // [itest->dsn~waiting-until-file-appears-in-target-directory~1]
    // [itest->dsn~validating-bucketfs-object-synchronization-via-the-bucketfs-log~1]
    @Test
    void testWaitForFileToAppear(@TempDir final Path tempDir)
            throws BucketAccessException, InterruptedException, IOException, TimeoutException {
        final String filename = "large-file.txt";
        final Path tempFile = tempDir.resolve(filename);
        GENERATOR.createRandomFile(tempFile, 10000);
        assertObjectSynchronized(tempFile, container.getDefaultBucket(), filename);
    }

    private void assertObjectSynchronized(final Path tempFile, final Bucket bucket, final String pathInBucket)
            throws BucketAccessException, InterruptedException, TimeoutException {
        final Instant now = Instant.now();
        assertThat(bucket.isObjectSynchronized(pathInBucket, now), equalTo(false));
        bucket.uploadFile(tempFile, pathInBucket);
        assertThat(bucket.isObjectSynchronized(pathInBucket, now), equalTo(true));
    }

    // [itest->dsn~waiting-until-archive-extracted~1]
    // [itest->dsn~validating-bucketfs-object-synchronization-via-the-bucketfs-log~1]
    @Test
    void testWaitForArchiveToBeExtracted(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, InterruptedException, TimeoutException {
        final String filename = "archive.zip";
        final Path tempFile = tempDir.resolve(filename);
        createArchive(tempFile);
        assertObjectSynchronized(tempFile, container.getDefaultBucket(), filename);
    }

    private void createArchive(final Path file) throws FileNotFoundException, IOException {
        final ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file.toFile()));
        final ZipEntry entry = new ZipEntry("random.txt");
        zip.putNextEntry(entry);
        zip.write("Random bytes:\n".getBytes());
        GENERATOR.writeRandomBytesToStream(zip, 10000);
        zip.closeEntry();
        zip.close();
    }
}