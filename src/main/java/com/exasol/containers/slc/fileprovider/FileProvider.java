package com.exasol.containers.slc.fileprovider;

import java.nio.file.Path;

import com.exasol.containers.slc.ScriptLanguageContainer;
import com.exasol.errorreporting.ExaError;

/**
 * This interface provides access to local files for a {@link ScriptLanguageContainer}, independent if the SLC uses a
 * local file or a {@code URL}.
 */
public interface FileProvider {

    /**
     * Create a new {@link FileProvider} for a given {@link ScriptLanguageContainer}, depending on the configuration.
     * 
     * @param slc the {@link ScriptLanguageContainer}
     * @return new {@link FileProvider}
     */
    public static FileProvider forSlc(final ScriptLanguageContainer slc) {
        final FileProvider provider = createProvider(slc);
        if (slc.getSha512sum() != null) {
            return new ChecksumVerifyingFileProvider(provider, slc.getSha512sum());
        }
        return provider;
    }

    private static FileProvider createProvider(final ScriptLanguageContainer slc) {
        if (slc.getLocalFile() != null && slc.getUrl() != null) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-41")
                    .message("SLC must have either a local file or a URL, not both").toString());
        }
        if (slc.getLocalFile() != null) {
            return new LocalFileProvider(slc.getLocalFile());
        } else if (slc.getUrl() != null) {
            return new CachingUrlFileProvider(slc.getUrl());
        } else {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-39")
                    .message("SLC must have either a local file or a URL").toString());
        }
    }

    /**
     * Get the local path. If necessary this will download the file.
     * 
     * @return local path
     */
    Path getLocalFile();

    /**
     * Get the name of the file.
     * 
     * @return file name
     */
    String getFileName();
}
