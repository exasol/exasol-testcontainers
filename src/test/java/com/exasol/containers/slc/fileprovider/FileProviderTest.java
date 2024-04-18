package com.exasol.containers.slc.fileprovider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.MalformedURLException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.exasol.containers.slc.ScriptLanguageContainer;
import com.exasol.containers.slc.ScriptLanguageContainer.Builder;
import com.exasol.containers.slc.ScriptLanguageContainer.Language;

class FileProviderTest {

    @Test
    void forSlcReturnsLocalFileProvider() {
        final FileProvider provider = create(slcBuilder().localFile(Paths.get("test")));
        assertThat(provider, instanceOf(LocalFileProvider.class));
    }

    @Test
    void forSlcLocalFileProviderWithChecksumVerifier() {
        final FileProvider provider = create(slcBuilder().localFile(Paths.get("test")).sha512sum("checksum"));
        assertThat(provider, instanceOf(ChecksumVerifyingFileProvider.class));
    }

    @Test
    void forSlcReturnsUrlProvider() throws MalformedURLException {
        final FileProvider provider = create(slcBuilder().url("http://0.0.0.0/file"));
        assertThat(provider, instanceOf(CachingUrlFileProvider.class));
    }

    @Test
    void forSlcReturnsUrlProviderWithChecksumVerifier() throws MalformedURLException {
        final FileProvider provider = create(slcBuilder().url("http://0.0.0.0/file").sha512sum("checksum"));
        assertThat(provider, instanceOf(ChecksumVerifyingFileProvider.class));
    }

    @Test
    void forSlcFailsForMissingFileAndUrl() throws MalformedURLException {
        final Builder slcBuilder = slcBuilder();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> create(slcBuilder));
        assertThat(exception.getMessage(), equalTo("E-ETC-39: SLC must have either a local file or a URL"));
    }

    private FileProvider create(final ScriptLanguageContainer.Builder slcBuilder) {
        return FileProvider.forSlc(slcBuilder.build());
    }

    private Builder slcBuilder() {
        return ScriptLanguageContainer.builder().language(Language.JAVA);
    }
}
