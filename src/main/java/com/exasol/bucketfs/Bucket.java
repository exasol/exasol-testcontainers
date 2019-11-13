package com.exasol.bucketfs;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstraction for a bucket inside Exasol's BucketFS.
 */
public class Bucket {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bucket.class);
    private final String bucketFsName;
    private final String bucketName;
    private final String ipAddress;
    private final int port;
    private final String readPassword;
    private final String writePassword;
    private final HttpClient client = HttpClient.newBuilder().build();

    private Bucket(final Builder builder) {
        this.bucketFsName = builder.bucketFsName;
        this.bucketName = builder.bucketName;
        this.ipAddress = builder.ipAddress;
        this.port = builder.port;
        this.readPassword = builder.readPassword;
        this.writePassword = builder.writePassword;
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
     * List the contents of a path inside a bucket
     *
     * @param path relative path from the bucket root
     * @return list of file system entries
     * @throws BucketAccessException if the contents are not accessible or the path is invalid
     * @throws InterruptedException  if the list request was interrupted
     */
    // [impl->dsn~bucket-lists-its-contents~1]
    public Set<String> listContents(final String path) throws BucketAccessException, InterruptedException {
        final URI uri = createPublicReadURI(path);
        LOGGER.debug("Listing contents of bucket path \"{}\"", uri);
        try {
            final HttpRequest request = HttpRequest.newBuilder(uri).build();
            final HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return Set.of(response.body().split("\\s+"));
            } else {
                throw new BucketAccessException("Unable to list contents of bucket.", response.statusCode(), uri);
            }
        } catch (final IOException exception) {
            throw new BucketAccessException("Unable to list contents of bucket.", uri, exception);
        }
    }

    private URI createPublicReadURI(final String pathInBucket) {
        return URI.create("http://" + this.ipAddress + ":" + this.port + "/" + this.bucketName + "/" + pathInBucket);
    }

    /**
     * Upload a file to the bucket.
     *
     * @param pathInBucket path inside the bucket
     * @param localPath    path of the file to be uploaded
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     */
    // [impl->dsn~uploading-to-bucket~1]
    public void uploadFile(final Path localPath, final String pathInBucket)
            throws IOException, InterruptedException, BucketAccessException {
        final URI uri = createWriteURI(pathInBucket);
        LOGGER.info("Uploading file \"{}\" to \"{}\"", localPath, uri);
        try {
            final int statusCode = httpPut(uri, BodyPublishers.ofFile(localPath));
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new BucketAccessException("Unable to upload file \"" + localPath + "\"" + " to ", statusCode,
                        uri);
            }
        } catch (final IOException exception) {
            throw new BucketAccessException("Unable to upload file \"" + localPath + "\"" + " to ", uri, exception);
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

    private URI createWriteURI(final String pathInBucket) throws BucketAccessException {
        try {
            return new URI("http", null, this.ipAddress, this.port, "/" + this.bucketName + "/" + pathInBucket, null,
                    null);
        } catch (final URISyntaxException exception) {
            throw new BucketAccessException("Unable to create write URI.", exception);
        }
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
     *
     * @param pathInBucket path inside the bucket
     * @param localPath    path of the file to be uploaded
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     */
    // [impl->dsn~uploading-strings-to-bucket~1]
    public void uploadStringContent(final String content, final String pathInBucket)
            throws IOException, InterruptedException, BucketAccessException {
        final String excerpt = (content.length() > 20) ? content.substring(0, 20) + "..." : content;
        final URI uri = createWriteURI(pathInBucket);
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
         * @param ipAddress IP Address the BucketFS service listens on
         * @return Builder instance for fluent programming
         */
        public Builder httpPort(final int port) {
            this.port = port;
            return this;
        }

        /**
         * Set the read password
         *
         * @param readPassword
         * @return Builder instance for fluent programming
         */
        public Builder readPassword(final String readPassword) {
            this.readPassword = readPassword;
            return this;
        }

        /**
         * Set the write password
         *
         * @param writePassword
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