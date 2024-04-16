package com.exasol.containers.slc;

import java.nio.file.Path;

public class ScriptLanguageContainer {

    private final Language language;
    private final String alias;
    private final Path localFile;

    private ScriptLanguageContainer(final Builder builder) {
        this.language = builder.language;
        this.alias = builder.alias;
        this.localFile = builder.localFile;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Language language;
        private String alias;
        private Path localFile;

        public Builder language(final Language language) {
            this.language = language;
            return this;
        }

        public Builder alias(final String alias) {
            this.alias = alias;
            return this;
        }

        public Builder localFile(final Path localFile) {
            this.localFile = localFile;
            return this;
        }

        public ScriptLanguageContainer build() {
            return new ScriptLanguageContainer(this);
        }
    }

    public enum Language {
        JAVA, R, PYTHON
    }
}
