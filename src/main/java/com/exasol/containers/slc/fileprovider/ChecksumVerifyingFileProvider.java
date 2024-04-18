package com.exasol.containers.slc.fileprovider;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.exasol.errorreporting.ExaError;

public class ChecksumVerifyingFileProvider implements FileProvider {

    private final FileProvider delegate;
    private final String expectedSha512sum;

    ChecksumVerifyingFileProvider(final FileProvider delegate, final String sha512sum) {
        this.delegate = delegate;
        this.expectedSha512sum = sha512sum;
    }

    @Override
    public Path getLocalFile() {
        final Path localFile = this.delegate.getLocalFile();
        final String calculatedChecksum = calculateSha512sum(localFile);
        if (calculatedChecksum.equalsIgnoreCase(this.expectedSha512sum)) {
            return localFile;
        } else {
            throw new IllegalStateException(ExaError.messageBuilder("E-ETC-37").message(
                    "Sha512 checksum verification failed for file {{file}}, expected {{expected checksum}} but got {{calculated checksum}}",
                    localFile, expectedSha512sum, calculatedChecksum).toString());
        }
    }

    private static String calculateSha512sum(final Path localFile) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(Files.readAllBytes(localFile));
            final byte[] digest = md.digest();
            final BigInteger bigInt = new BigInteger(1, digest);
            String sha512 = bigInt.toString(16);
            while (sha512.length() < 64) {
                sha512 = "0" + sha512;
            }
            return sha512;
        } catch (NoSuchAlgorithmException | IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    public String getFileName() {
        return delegate.getFileName();
    }
}
