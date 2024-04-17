package com.exasol.containers.slc;

import java.nio.file.Path;
import java.util.Objects;

/**
 * This represents an Exasol Script Language Container (SLC) that can be installed on an Exasol database. A SLC allows
 * running User Defined Functions (UDFs) in a specific language.
 */
public final class ScriptLanguageContainer {
    private final Language language;
    private final String alias;
    private final Path localFile;

    private ScriptLanguageContainer(final Builder builder) {
        this.language = Objects.requireNonNull(builder.language, "language");
        this.alias = Objects.requireNonNull(builder.alias, "alias");
        this.localFile = Objects.requireNonNull(builder.localFile, "localFile");
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
        return alias;
    }

    /**
     * @return the local file of the SLC
     */
    public Path getLocalFile() {
        return localFile;
    }

    @Override
    public String toString() {
        return "ScriptLanguageContainer [language=" + language + ", alias=" + alias + ", localFile=" + localFile + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, alias, localFile);
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
                && Objects.equals(localFile, other.localFile);
    }

    /**
     * A builder class for {@link ScriptLanguageContainer} instances.
     */
    public static class Builder {
        private Language language;
        private String alias;
        private Path localFile;

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
         * Set the alias of the SLC.
         * 
         * @param alias alias of the SLC
         * @return {@code this} for fluent programming
         */
        public Builder alias(final String alias) {
            this.alias = alias;
            return this;
        }

        /**
         * Set the local file of the SLC.
         * 
         * @param localFile local file of the SLC
         * @return {@code this} for fluent programming
         */
        public Builder localFile(final Path localFile) {
            this.localFile = localFile;
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
        JAVA,
        /** SLC supports running R UDFs */
        R,
        /** SLC supports running Python UDFs */
        PYTHON
    }
}
