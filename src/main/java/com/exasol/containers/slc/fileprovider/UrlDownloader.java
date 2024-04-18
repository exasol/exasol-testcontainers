package com.exasol.containers.slc.fileprovider;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.errorreporting.ExaError;

// [impl->dsn~install-custom-slc.url~1]
class UrlDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlDownloader.class);

    void download(final URL url, final Path targetPath) {
        LOGGER.debug("Downloading URL {} to {}...", url, targetPath);
        final Instant start = Instant.now();
        try (InputStream in = url.openStream()) {
            final long fileSize = Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            final Duration duration = Duration.between(start, Instant.now());
            LOGGER.debug("Downloaded {} with size {} bytes in {}.", targetPath, fileSize, duration);
        } catch (final IOException exception) {
            throw new UncheckedIOException(ExaError.messageBuilder("E-ETC-38").message(
                    "Failed to download file from URL {{url}} to target path {{target path}}: {{error message|uq}}",
                    url, targetPath, exception.toString()).toString(), exception);
        }
    }
}
