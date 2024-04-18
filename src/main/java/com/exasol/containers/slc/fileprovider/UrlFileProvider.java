package com.exasol.containers.slc.fileprovider;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UrlFileProvider implements FileProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlFileProvider.class);
    private static final Path CACHE_DIR = Path.of(System.getProperty("user.home"))
            .resolve(".cache/exasol-testcontainers");

    private final UrlDownloader urlDownloader;
    private final URL url;

    UrlFileProvider(final URL url) {
        this(new UrlDownloader(), url);
    }

    UrlFileProvider(final UrlDownloader urlDownloader, final URL url) {
        this.urlDownloader = urlDownloader;
        this.url = url;
    }

    @Override
    public Path getLocalFile() {
        final Path localPath = getCachePath();
        if (Files.exists(localPath)) {
            LOGGER.debug("Using cached file {}", localPath);
            return localPath;
        }
        urlDownloader.download(url, localPath);
        return localPath;
    }

    @Override
    public String getFileName() {
        return getUrlFileName(url);
    }

    private Path getCachePath() {
        return CACHE_DIR.resolve(getUrlFileName(url));
    }

    static String getUrlFileName(final URL url) {
        final String path = url.getPath();
        return url.getPath().substring(path.lastIndexOf('/') + 1);
    }
}
