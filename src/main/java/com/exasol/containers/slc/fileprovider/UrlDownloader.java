package com.exasol.containers.slc.fileprovider;

import java.io.*;
import java.net.URL;
import java.nio.file.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.errorreporting.ExaError;

class UrlDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlDownloader.class);

    void download(final URL url, final Path targetPath) {
        LOGGER.debug("Downloading URL {} to {}...", url, targetPath);
        try (InputStream in = url.openStream()) {
            Files.createDirectories(targetPath.getParent());
            final long fileSize = Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.debug("Downloaded {} with size {} bytes.", targetPath, fileSize);
        } catch (final IOException exception) {
            throw new UncheckedIOException(ExaError.messageBuilder("E-ETC-38").message(
                    "Failed to download file from URL {{url}} to target path {{target path}}: {{error message}}", url,
                    targetPath, exception.getMessage()).toString(), exception);
        }
    }
}
