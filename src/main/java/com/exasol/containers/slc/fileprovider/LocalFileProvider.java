package com.exasol.containers.slc.fileprovider;

import java.nio.file.Path;

// [impl->dsn~install-custom-slc.local-file~1]
class LocalFileProvider implements FileProvider {
    private final Path localFile;

    LocalFileProvider(final Path localFile) {
        this.localFile = localFile;
    }

    @Override
    public Path getLocalFile() {
        return this.localFile;
    }

    @Override
    public String getFileName() {
        return this.localFile.getFileName().toString();
    }
}
