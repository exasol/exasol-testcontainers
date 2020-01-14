package com.exasol.bucketfs;

import static com.exasol.containers.ExasolContainerConstants.*;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;

/**
 * An abstraction for a bucket inside Exasol's BucketFS.
 */
public class Bucket {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bucket.class);
    private static final String BUCKET_ROOT = "";
    private static final long BUCKET_SYNC_TIMEOUT_IN_MILLISECONDS = 60000;
    private static final long FILE_SYNC_POLLING_DELAY_IN_MILLISECONDS = 200;
    private final String bucketFsName;
    private final String bucketName;
    private final String ipAddress;
    private final int port;
    private final String readPassword;
    private final String writePassword;
    private final HttpClient client = HttpClient.newBuilder().build();
    private final LogPatternDetectorFactory detectorFactory;

    private Bucket(final Builder builder) {
        this.bucketFsName = builder.bucketFsName;
        this.bucketName = builder.bucketName;
        this.ipAddress = builder.ipAddress;
        this.port = builder.port;
        this.readPassword = builder.readPassword;
        this.writePassword = builder.writePassword;
        this.detectorFactory = builder.detectorFactory;
    }

    /**
     * @return name of the BucketFS filesystem this bucket belongs to
     */
    public String getBucketFsName() {
        return this.bucketFsName;
    }

    /**
     * @return name of the bucket
     */
    public String getBucketName() {
        return this.bucketName;
    }

    /**
     * Get the read password for the bucket.
     *
     * @return read password
     */
    public String getReadPassword() {
        return this.readPassword;
    }

    /**
     * Get the write password for the bucket.
     *
     * @return write password.
     */
    public String getWritePassword() {
        return this.writePassword;
    }

    /**
     * List the contents of a bucket.
     *
     * @return bucket contents
     * @throws BucketAccessException if the contents are not accessible or the path is invalid
     * @throws InterruptedException  if the list request was interrupted
     */
    public List<String> listContents() throws BucketAccessException, InterruptedException {
        return listContents(BUCKET_ROOT);
    }

    /**
     * List the contents of a path inside a bucket.
     *
     * @param path relative path from the bucket root
     * @return list of file system entries
     * @throws BucketAccessException if the contents are not accessible or the path is invalid
     * @throws InterruptedException  if the list request was interrupted
     */
    // [impl->dsn~bucket-lists-its-contents~1]
    public List<String> listContents(final String path) throws BucketAccessException, InterruptedException {
        final URI uri = createPublicReadURI(BUCKET_ROOT);
        LOGGER.debug("Listing contents of bucket under URI \"{}\"", uri);
        try {
            final HttpRequest request = HttpRequest.newBuilder(uri).build();
            final HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return parseContentListResponseBody(response, removeLeadingSlash(path));
            } else {
                throw new BucketAccessException("Unable to list contents of bucket.", response.statusCode(), uri);
            }
        } catch (final IOException exception) {
            throw new BucketAccessException("Unable to list contents of bucket.", uri, exception);
        }
    }

    /**
     * Check whether the file with the given path exists in this bucket.
     *
     * @param pathInBucket relative path from the bucket root
     * @param afterUTC     UTC time in milliseconds after which the object synchronization must happen
     * @return {@code true} if the file exists in the bucket.
     * @throws InterruptedException  if the synchronization check is interrupted
     * @throws BucketAccessException if an I/O operation failed in the underlying check
     */
    public boolean isObjectSynchronized(final String pathInBucket, final Instant afterUTC)
            throws InterruptedException, BucketAccessException {
        try {
            final String pattern = pathInBucket + ".*"
                    + (isSupportedArchiveFormat(pathInBucket) ? "extracted" : "linked");
            final LogPatternDetector detector = this.detectorFactory.createLogPatternDetector(
                    EXASOL_CORE_DAEMON_LOGS_PATH, BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, pattern);
            return detector.isPatternPresentAfter(afterUTC);
        } catch (final IOException exception) {
            throw new BucketAccessException("Unable to check if object \"" + pathInBucket
                    + "\" is synchronized in bucket \"" + this.bucketFsName + "/" + this.bucketName + "\".", exception);
        }
    }

    private String removeLeadingSlash(final String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        } else {
            return path;
        }
    }

    private URI createPublicReadURI(final String pathInBucket) {
        return URI.create("http://" + this.ipAddress + ":" + this.port + "/" + this.bucketName + "/"
                + removeLeadingSlash(pathInBucket));
    }

    private List<String> parseContentListResponseBody(final HttpResponse<String> response, final String path) {
        final String[] items = response.body().split("\\s+");
        final List<String> contents = new ArrayList<>(items.length);
        for (final String item : items) {
            final String relativeItem = removeLeadingSlash(item);
            if (relativeItem.startsWith(path)) {
                contents.add(extractFirstPathComponent(relativeItem.substring(path.length(), relativeItem.length())));
            }
        }
        return contents;
    }

    private String extractFirstPathComponent(final String path) {
        if (path.contains("/")) {
            return path.substring(0, path.indexOf('/'));
        } else {
            return path;
        }
    }

    /**
     * Upload a file to the bucket.
     * <p>
     * Uploads a file from a given local path to a URI pointing to a BucketFS bucket. If the bucket URI ends in a slash,
     * that URI is interpreted as a directory inside the bucket and the original filename is appended.
     * </p>
     * <p>
     * This call blocks until the uploaded file is synchronized in BucketFs or a timeout occurs.
     * </p>
     *
     * @param pathInBucket path inside the bucket
     * @param localPath    path of the file to be uploaded
     * @throws TimeoutException      if the synchronization check takes too long
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     */
    // [impl->dsn~uploading-to-bucket~1]
    public void uploadFile(final Path localPath, final String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException {
        uploadFile(localPath, pathInBucket, true);
    }

    /**
     * Upload a file to the bucket and block the call until it is synchronized.
     * <p>
     * Uploads a file from a given local path to a URI pointing to a BucketFS bucket. If the bucket URI ends in a slash,
     * that URI is interpreted as a directory inside the bucket and the original filename is appended.
     * </p>
     * <p>
     * When blocking is enabled, this call waits until either the uploaded file is synchronized or a timeout occurred.
     * </p>
     *
     * @param pathInBucket path inside the bucket
     * @param localPath    path of the file to be uploaded
     * @param blocking     when set to {@code true}, the call waits for the uploaded object to be synchronized,
     *                     otherwise immediately returns
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws TimeoutException      if synchronization takes too long
     */
    public void uploadFile(final Path localPath, final String pathInBucket, final boolean blocking)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final String extendedPathInBucket = extendPathInBucketDownToFilename(localPath, pathInBucket);
        uploadFileNonBlocking(localPath, extendedPathInBucket);
        if (blocking) {
            waitForFileToBeSynchronized(extendedPathInBucket);
        }
    }

    private String extendPathInBucketDownToFilename(final Path localPath, final String pathInBucket) {
        return pathInBucket.endsWith("/") ? pathInBucket + localPath.getFileName() : pathInBucket;
    }

    private void uploadFileNonBlocking(final Path localPath, final String pathInBucket)
            throws InterruptedException, BucketAccessException {
        final URI uri = createWriteUri(pathInBucket);
        LOGGER.info("Uploading file \"{}\" to bucket \"{}/{}\": \"{}\"", localPath, this.bucketFsName, this.bucketName,
                uri);
        try {
            final int statusCode = httpPut(uri, BodyPublishers.ofFile(localPath));
            if (statusCode != HttpURLConnection.HTTP_OK) {
                LOGGER.error("{}: Failed to upload file \"{}\" to \"{}\"", statusCode, localPath, uri);
                throw new BucketAccessException("Unable to upload file \"" + localPath + "\"" + " to ", statusCode,
                        uri);
            }
        } catch (final IOException exception) {
            throw new BucketAccessException("Unable to upload file \"" + localPath + "\"" + " to ", uri, exception);
        }
        LOGGER.info("Successfully uploaded to \"{}\"", uri);
    }

    private URI createWriteUri(final String pathInBucket) throws BucketAccessException {
        try {
            return new URI("http", null, this.ipAddress, this.port, "/" + this.bucketName + "/" + pathInBucket, null,
                    null).normalize();
        } catch (final URISyntaxException exception) {
            throw new BucketAccessException("Unable to create write URI.", exception);
        }
    }

    private int httpPut(final URI uri, final BodyPublisher bodyPublisher) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(uri) //
                .PUT(bodyPublisher) //
                .header("Authorization", encodeBasicAuth(true)) //
                .build();
        final HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
        return response.statusCode();
    }

    private String encodeBasicAuth(final boolean write) {
        return "Basic " + Base64.getEncoder() //
                .encodeToString((write ? ("w:" + this.writePassword) : ("r:" + this.readPassword)).getBytes());
    }

    /**
     * Upload the contents of a string to the bucket.
     * <p>
     * This method is intended for writing small objects in BucketFS dynamically like for example configuration files.
     * For large payload use {@link Bucket#uploadFile(Path, String)} instead.
     * </p>
     * <p>
     * This call blocks until the uploaded file is synchronized in BucketFs or a timeout occurs.
     * </p>
     *
     * @param content      string to write
     * @param pathInBucket path inside the bucket
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws TimeoutException      if synchronization takes too long
     */
    // [impl->dsn~uploading-strings-to-bucket~1]
    public void uploadStringContent(final String content, final String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException {
        uploadStringContent(content, pathInBucket, true);
    }

    /**
     * Upload the contents of a string to the bucket.
     * <p>
     * This method is intended for writing small objects in BucketFS dynamically like for example configuration files.
     * For large payload use {@link Bucket#uploadFile(Path, String)} instead.
     * </p>
     * <p>
     * This call blocks until the uploaded file is synchronized in BucketFs or a timeout occurs.
     * </p>
     *
     * @param content      string to write
     * @param pathInBucket path inside the bucket
     * @param blocking     when set to {@code true}, the call waits for the uploaded object to be synchronized,
     *                     otherwise immediately returns
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws TimeoutException      if synchronization takes too long
     */
    public void uploadStringContent(final String content, final String pathInBucket, final boolean blocking)
            throws InterruptedException, BucketAccessException, TimeoutException {
        uploadStringContentNonBlocking(content, pathInBucket);
        if (blocking) {
            waitForFileToBeSynchronized(pathInBucket);
        }
    }

    private void uploadStringContentNonBlocking(final String content, final String pathInBucket)
            throws InterruptedException, BucketAccessException {
        final String excerpt = (content.length() > 20) ? content.substring(0, 20) + "..." : content;
        final URI uri = createWriteUri(pathInBucket);
        LOGGER.info("Uploading text \"{}\" to \"{}\"", excerpt, uri);
        try {
            final int statusCode = httpPut(uri, BodyPublishers.ofString(content));
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new BucketAccessException("Unable to upload text \"" + excerpt + "\"" + " to bucket.", statusCode,
                        uri);
            }
        } catch (final IOException exception) {
            throw new BucketAccessException("Unable to upload text \"" + excerpt + "\"" + " to bucket.", uri,
                    exception);
        }
    }

    private boolean isSupportedArchiveFormat(final String pathInBucket) {
        for (final String extension : SUPPORTED_ARCHIVE_EXTENSIONS) {
            if (pathInBucket.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    // [impl->dsn~waiting-until-archive-extracted~1]
    // [impl->dsn~waiting-until-file-appears-in-target-directory~1]
    private void waitForFileToBeSynchronized(final String pathInBucket)
            throws InterruptedException, TimeoutException, BucketAccessException {
        final Instant now = Instant.now();
        final Instant expiry = now.plusMillis(BUCKET_SYNC_TIMEOUT_IN_MILLISECONDS);
        while (Instant.now().isBefore(expiry)) {
            if (isObjectSynchronized(pathInBucket, now)) {
                return;
            }
            Thread.sleep(FILE_SYNC_POLLING_DELAY_IN_MILLISECONDS);
        }
        throw new TimeoutException("Timeout waiting for object \"" + pathInBucket + "\"to be synchronized in bucket \""
                + this.bucketFsName + "/" + this.bucketName + "\".");
    }

    /**
     * Create builder for a {@link Bucket}.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link Bucket} objects.
     */
    public static class Builder {
        private String bucketFsName;
        private String bucketName;
        private String ipAddress;
        private int port;
        private String readPassword;
        private String writePassword;
        private LogPatternDetectorFactory detectorFactory;

        /**
         * Set the log pattern detector factory.
         *
         * @param detectorFactory factory for log pattern detectors
         * @return Builder instance for fluent programming
         */
        public Builder detectorFactory(final LogPatternDetectorFactory detectorFactory) {
            this.detectorFactory = detectorFactory;
            return this;
        }

        /**
         * Set the filesystem name.
         *
         * @param bucketFsName name of the BucketFS filesystem
         * @return Builder instance for fluent programming
         */
        public Builder serviceName(final String bucketFsName) {
            this.bucketFsName = bucketFsName;
            return this;
        }

        /**
         * Set the bucket name.
         *
         * @param bucketName name of the bucket
         * @return Builder instance for fluent programming
         */
        public Builder name(final String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        /**
         * Set the IP address of the BucketFS service.
         *
         * @param ipAddress IP Address of the BucketFS service
         * @return Builder instance for fluent programming
         */
        public Builder ipAddress(final String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        /**
         * Set the port the BucketFS service listens on.
         *
         * @param port HTTP port the BucketFS service listens on
         * @return Builder instance for fluent programming
         */
        public Builder httpPort(final int port) {
            this.port = port;
            return this;
        }

        /**
         * Set the read password.
         *
         * @param readPassword read password to set
         * @return Builder instance for fluent programming
         */
        public Builder readPassword(final String readPassword) {
            this.readPassword = readPassword;
            return this;
        }

        /**
         * Set the write password.
         *
         * @param writePassword write password to set
         * @return Builder instance for fluent programming
         */
        public Builder writePassword(final String writePassword) {
            this.writePassword = writePassword;
            return this;
        }

        /**
         * Build a new {@link Bucket} instance.
         *
         * @return bucket instance
         */
        public Bucket build() {
            return new Bucket(this);
        }
    }
}