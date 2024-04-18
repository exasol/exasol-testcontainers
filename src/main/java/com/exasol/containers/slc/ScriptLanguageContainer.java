package com.exasol.containers.slc;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import com.exasol.errorreporting.ExaError;

/**
 * This represents an Exasol Script Language Container (SLC) that can be installed on an Exasol database. A SLC allows
 * running User Defined Functions (UDFs) in a specific language.
 */
public final class ScriptLanguageContainer implements Serializable {
    private static final long serialVersionUID = -3295522116885302191L;
    /** @serial */
    private final Language language;
    /** @serial */
    private final String alias;
    /** @serial */
    private final String udfEntryPoint;
    /** @serial We use type String instead of Path, because Path is not serializable. */
    private final String localFile;
    /** @serial */
    private final String url;
    /** @serial */
    private final String sha512sum;

    private ScriptLanguageContainer(final Builder builder) {
        this.language = Objects.requireNonNull(builder.language, "language");
        this.alias = builder.alias;
        this.localFile = builder.localFile;
        this.url = builder.url;
        this.sha512sum = builder.sha512sum;
        this.udfEntryPoint = builder.udfEntryPoint;
    }

    /**
     * Create a new {@link Builder} for a {@link ScriptLanguageContainer}.
     * 
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return the UDF language of the SLC
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * @return the alias of the SLC
     */
    public String getAlias() {
        if (this.alias != null) {
            return alias;
        }
        return language.getDefaultAlias();
    }

    /**
     * @return the path of the UDF entry point
     */
    public String getUdfEntryPoint() {
        if (this.udfEntryPoint != null) {
            return udfEntryPoint;
        }
        return language.getDefaultUdfEntryPoint();
    }

    /**
     * Get the local file of the SLC. This is only set if {@link getUrl} returns {@code null}.
     * 
     * @return the local file of the SLC
     */
    public Path getLocalFile() {
        return Optional.ofNullable(localFile).map(Path::of).orElse(null);
    }

    /**
     * Get the URL of the SLC. This is only set if {@link getLocalFile()} returns {@code null}.
     * 
     * @return URL of the SLC
     */
    public URL getUrl() {
        return Optional.ofNullable(url).map(ScriptLanguageContainer::parseUrl).orElse(null);
    }

    private static URL parseUrl(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException exception) {
            throw new IllegalArgumentException(
                    ExaError.messageBuilder("E-ETC-36").message("Invalid SLC URL: {{url}}", url).toString(), exception);
        }
    }

    /**
     * Get the sha512sum of the SLC. This is required for SLCs downloaded from the internet.
     * 
     * @return sha512sum of the SLC
     */
    public String getSha512sum() {
        return this.sha512sum;
    }

    @Override
    public String toString() {
        return "ScriptLanguageContainer [language=" + language + ", alias=" + alias + ", udfEntryPoint=" + udfEntryPoint
                + ", localFile=" + localFile + ", url=" + url + ", sha512sum=" + sha512sum + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, alias, localFile, udfEntryPoint, url, sha512sum);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ScriptLanguageContainer other = (ScriptLanguageContainer) obj;
        return language == other.language && Objects.equals(alias, other.alias)
                && Objects.equals(udfEntryPoint, other.udfEntryPoint) && Objects.equals(localFile, other.localFile)
                && Objects.equals(url, other.url) && Objects.equals(sha512sum, other.sha512sum);
    }

    /**
     * A builder class for {@link ScriptLanguageContainer} instances.
     */
    public static class Builder {
        private String udfEntryPoint;
        private Language language;
        private String alias;
        private String localFile;
        private String url;
        private String sha512sum;

        private Builder() {
            // prevent instantiation from outside
        }

        /**
         * Set the UDF language of the SLC.
         * 
         * @param language UDF language
         * @return {@code this} for fluent programming
         */
        public Builder language(final Language language) {
            this.language = language;
            return this;
        }

        /**
         * Set the alias of the SLC. If this is not set, the default alias for the language will be used. Please note
         * that using the default alias will overwrite the built-in SLC for that language.
         * <p>
         * Default values:
         * <ul>
         * <li>Java: {@code JAVA}</li>
         * <li>R: {@code R}</li>
         * <li>Python: {@code PYTHON3}</li>
         * </ul>
         * 
         * @param alias alias of the SLC
         * @return {@code this} for fluent programming
         */
        public Builder alias(final String alias) {
            this.alias = alias;
            return this;
        }

        /**
         * Set the path of the entry point for the User Defined Function (UDF). If not set, the default entry point for
         * the language will be used.
         * <p>
         * Default values:
         * <ul>
         * <li>Java, R: {@code /exaudf/exaudfclient}</li>
         * <li>Python 3: {@code /exaudf/exaudfclient_py3}</li>
         * </ul>
         *
         * @param udfEntryPoint entry point for the UDF
         * @return {@code this} for fluent programming
         */
        public Builder udfEntryPoint(final String udfEntryPoint) {
            this.udfEntryPoint = udfEntryPoint;
            return this;
        }

        /**
         * Set the local file of the SLC. Use this method if the SLC is available as a local file.
         * <p>
         * This is an alternative to {@link #url(String)}.
         * 
         * @param localFile local file of the SLC
         * @return {@code this} for fluent programming
         */
        // [impl->dsn~install-custom-slc.local-file~1]
        public Builder localFile(final Path localFile) {
            this.localFile = localFile.toString();
            return this;
        }

        /**
         * Set the URL of the SLC. Use this method if the SLC is not available as a local file but can be downloaded
         * from a server.
         * <p>
         * This is an alternative to {@link #localFile(Path)}.
         * 
         * 
         * @param url url of the SLC
         * @return {@code this} for fluent programming
         */
        // [impl->dsn~install-custom-slc.url~1]
        public Builder url(final String url) {
            this.url = url;
            return this;
        }

        /**
         * Use a released SLC from <a href=
         * "https://github.com/exasol/script-languages-release/releases">https://github.com/exasol/script-languages-release</a>.
         * <p>
         * This is a convenience method that sets the URL using {@link #url(String)} based on version and file name.
         * 
         * @param version  version of the SLC release
         * @param fileName file name of the SLC
         * @return {@code this} for fluent programming
         */
        // [impl->dsn~install-custom-slc.url~1]
        public Builder slcRelease(final String version, final String fileName) {
            return url("https://extensions-internal.exasol.com/com.exasol/script-languages-release/" + version + "/"
                    + fileName);
        }

        /**
         * Set the sha512sum of the SLC. This is required for SLCs when {@link #url(String)} or
         * {@link #slcRelease(String, String)} was used.
         * 
         * @param sha512sum sha512sum of the SLC
         * @return {@code this} for fluent programming
         */
        // [impl->dsn~install-custom-slc.verify-checksum~1]
        public Builder sha512sum(final String sha512sum) {
            this.sha512sum = sha512sum;
            return this;
        }

        /**
         * Build the {@link ScriptLanguageContainer}.
         * 
         * @return the built {@link ScriptLanguageContainer}
         */
        public ScriptLanguageContainer build() {
            return new ScriptLanguageContainer(this);
        }
    }

    /**
     * The UDF language supported by the SLC.
     */
    public enum Language {
        /** SLC supports running Java UDFs */
        JAVA("java", "JAVA", "/exaudf/exaudfclient"),
        /** SLC supports running R UDFs */
        R("r", "R", "/exaudf/exaudfclient"),
        /** SLC supports running Python UDFs */
        PYTHON("python", "PYTHON3", "/exaudf/exaudfclient_py3");

        private final String name;
        private final String defaultAlias;
        private final String defaultUdfEntryPoint;

        private Language(final String name, final String defaultAlias, final String defaultUdfEntryPoint) {
            this.name = name;
            this.defaultAlias = defaultAlias;
            this.defaultUdfEntryPoint = defaultUdfEntryPoint;
        }

        /**
         * Get the default UDF entry point for the language.
         * 
         * @return default UDF entry point
         */
        String getDefaultUdfEntryPoint() {
            return this.defaultUdfEntryPoint;
        }

        /**
         * Get the default alias for the language.
         * 
         * @return default alias
         */
        String getDefaultAlias() {
            return defaultAlias;
        }

        /**
         * Get the language name used for specifying the SLC.
         * 
         * @return name of the language
         */
        public String getName() {
            return name;
        }
    }
}
