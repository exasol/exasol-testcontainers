package com.exasol.containers.slc.fileprovider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.errorreporting.ExaError;

class ChecksumVerifyingFileProvider implements FileProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChecksumVerifyingFileProvider.class);
    private final FileProvider delegate;
    private final String expectedSha512sum;

    ChecksumVerifyingFileProvider(final FileProvider delegate, final String sha512sum) {
        this.delegate = delegate;
        this.expectedSha512sum = sha512sum;
    }

    // [impl->dsn~install-custom-slc.verify-checksum~1]
    @Override
    public Path getLocalFile() {
        final Path localFile = this.delegate.getLocalFile();
        final String calculatedChecksum = calculateSha512sum(localFile);
        if (calculatedChecksum.equalsIgnoreCase(this.expectedSha512sum)) {
            LOGGER.debug("Sha512 checksum of file '{}' is correct: {}", localFile, calculatedChecksum);
            return localFile;
        }
        throw new IllegalStateException(ExaError.messageBuilder("E-ETC-37").message(
                "Sha512 checksum verification failed for file {{file}}, expected {{expected checksum}} but got {{calculated checksum}}",
                localFile, expectedSha512sum, calculatedChecksum).toString());
    }

    private static String calculateSha512sum(final Path localFile) {
        final byte[] fileContent = readContent(localFile);
        final byte[] checksum = calculateSha512Checksum(fileContent);
        return formatChecksum(checksum);
    }

    private static byte[] readContent(final Path localFile) {
        try {
            return Files.readAllBytes(localFile);
        } catch (final IOException exception) {
            throw new UncheckedIOException("Failed to read file content from '" + localFile + "'", exception);
        }
    }

    private static byte[] calculateSha512Checksum(final byte[] content) {
        final MessageDigest md = createDigest();
        md.update(content);
        return md.digest();
    }

    private static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (final NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Failed to create SHA-512 message digest.", exception);
        }
    }

    private static String formatChecksum(final byte[] digest) {
        final BigInteger bigInt = new BigInteger(1, digest);
        return zeroPad(bigInt.toString(16));
    }

    @SuppressWarnings("java:S1643") // Using StringBuilder is not necessary here
    private static String zeroPad(String checksum) {
        while (checksum.length() < 64) {
            checksum = "0" + checksum;
        }
        return checksum;
    }

    @Override
    public String getFileName() {
        return delegate.getFileName();
    }
}
