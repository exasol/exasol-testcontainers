package com.exasol.containers.slc.fileprovider;

import java.nio.file.Path;

import com.exasol.containers.slc.ScriptLanguageContainer;

/**
 * This interface provides access to local files for a {@link ScriptLanguageContainer}, independent if the SLC uses a
 * local file or a {@code URL}.
 */
public interface FileProvider {

    public static FileProvider forSlc(final ScriptLanguageContainer slc) {
        final FileProvider provider = createProvider(slc);
        if (slc.getSha512sum() != null) {
            return new ChecksumVerifyingFileProvider(provider, slc.getSha512sum());
        }
        return provider;
    }

    private static FileProvider createProvider(final ScriptLanguageContainer slc) {
        if (slc.getLocalFile() != null) {
            return new LocalFileProvider(slc.getLocalFile());
        } else {
            return new UrlFileProvider(slc.getUrl());
        }
    }

    /**
     * Get the local path. If necessary this will download the file.
     * 
     * @return local path
     */
    Path getLocalFile();
}
