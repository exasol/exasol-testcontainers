package com.exasol.containers.slc.fileprovider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CachingUrlFileProvider implements FileProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachingUrlFileProvider.class);

    private final UrlDownloader urlDownloader;
    private final Path localCacheDir;
    private final URL url;

    CachingUrlFileProvider(final URL url) {
        this(new UrlDownloader(), getDefaultCacheDir(), url);
    }

    CachingUrlFileProvider(final UrlDownloader urlDownloader, final Path localCacheDir, final URL url) {
        this.urlDownloader = urlDownloader;
        this.localCacheDir = localCacheDir;
        this.url = url;
    }

    private static Path getDefaultCacheDir() {
        return Path.of(System.getProperty("user.home")).resolve(".cache/exasol-testcontainers");
    }

    @Override
    public Path getLocalFile() {
        final Path localPath = localCacheDir.resolve(getFileName());
        if (Files.exists(localPath)) {
            LOGGER.debug("Using cached file {}", localPath);
            return localPath;
        }
        createDirectories();
        urlDownloader.download(url, localPath);
        return localPath;
    }

    private void createDirectories() {
        if (Files.exists(localCacheDir)) {
            return;
        }
        try {
            Files.createDirectories(this.localCacheDir);
        } catch (final IOException exception) {
            throw new UncheckedIOException("Unable to create cache directory '" + this.localCacheDir + "'", exception);
        }
    }

    @Override
    public String getFileName() {
        final String path = url.getPath();
        return url.getPath().substring(path.lastIndexOf('/') + 1);
    }
}
