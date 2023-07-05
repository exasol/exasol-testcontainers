package com.exasol.drivers;

import java.nio.file.Path;

/**
 * The {@link JdbcDriver} represents a driver file and the related driver meta-information
 */
public class JdbcDriver implements DatabaseDriver {
    private static final long serialVersionUID = 8538768853929154698L;
    /** @serial */
    private final String name;
    /** @serial */
    private final String prefix;
    /** @serial */
    private final String sourcePath; // String instead of Path to allow serialization
    /** @serial */
    private final String mainClass;
    /** @serial */
    private final boolean securityManagerEnabled;

    private JdbcDriver(final Builder builder) {
        this.name = builder.name;
        this.prefix = builder.prefix;
        this.sourcePath = builder.localPath == null ? null : builder.localPath.toString();
        this.mainClass = builder.mainClass;
        this.securityManagerEnabled = builder.securityManagerEnabled;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the JDBC URL prefix.
     *
     * @return JDBC URL prefix
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Create a new instance of a {@link JdbcDriver}.
     *
     * @param name human-readable name the driver is listed under
     * @return builder for a JDBC driver
     */
    public static Builder builder(final String name) {
        return new Builder(name);
    }

    @Override
    public boolean hasSourceFile() {
        return this.sourcePath != null;
    }

    @Override
    public Path getSourcePath() {
        return Path.of(this.sourcePath);
    }

    @Override
    public String getFileName() {
        return this.getSourcePath().getFileName().toString();
    }

    /**
     * Determine if the Java Security Manager is enabled for this driver.
     *
     * @return {@code true} if the Security Manager is enabled
     */
    public boolean isSecurityManagerEnabled() {
        return this.securityManagerEnabled;
    }

    @Override
    public String toString() {
        return "JDBC driver \"" + this.name + "\" (" + this.mainClass + ")" //
                + (hasSourceFile() ? (", source: \"" + this.sourcePath + "\"") : "");
    }

    @Override
    public String getManifest() {
        return "DRIVERNAME=" + this.name + "\n" //
                + "JAR=" + this.getDriverFilename() + "\n" //
                + "DRIVERMAIN=" + this.mainClass + "\n" //
                + "PREFIX=" + this.prefix + "\n" //
                + "NOSECURITY=" + (this.securityManagerEnabled ? "NO" : "YES") + "\n" //
                + "FETCHSIZE=100000\n" //
                + "INSERTSIZE=-1\n"; // note that the trailing newline is mandatory!
    }

    private String getDriverFilename() {
        return Path.of(this.sourcePath).getFileName().toString();
    }

    /**
     * Builder for {@link JdbcDriver} instances.
     */
    public static class Builder {
        private final String name;
        private String prefix;
        private Path localPath;
        private String mainClass;
        private boolean securityManagerEnabled = true;

        /**
         * Create a new builder for a {@link JdbcDriver}.
         *
         * @param name driver name
         */
        public Builder(final String name) {
            this.name = name;
        }

        /**
         * Set the JDBC URL prefix.
         *
         * @param prefix JDBC URL prefix
         * @return builder instance for fluent programming
         */
        public Builder prefix(final String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Set the source file from which the driver should be installed.
         *
         * @param sourcePath path on the host where the file to be installed resides
         * @return builder instance for fluent programming
         */
        public Builder sourceFile(final Path sourcePath) {
            this.localPath = sourcePath;
            return this;
        }

        /**
         * Set the Java class that serves as entry point into the driver.
         *
         * @param mainClass driver's main class
         * @return builder instance for fluent programming
         */
        public Builder mainClass(final String mainClass) {
            this.mainClass = mainClass;
            return this;
        }

        /**
         * Switch the security manager on or off for this driver.
         *
         * @param enable set {@code true} if you want to enable the security manager for this driver
         * @return builder instance for fluent programming
         */
        public Builder enableSecurityManager(final boolean enable) {
            this.securityManagerEnabled = enable;
            return this;
        }

        /**
         * Build a new instance of a {@link JdbcDriver}.
         *
         * @return new JDBC driver
         */
        public JdbcDriver build() {
            validate();
            return new JdbcDriver(this);
        }

        private void validate() {
            if ((this.name == null) || !this.name.matches("[a-zA-Z]\\w+")) {
                throw new IllegalStateException("Empty or illegal driver name (" + this.name
                        + ") trying to build JDBC driver. The driver name must start with a letter "
                        + "followed by zero or more alphanumeric characters or underscores.");

            }
            if ((this.prefix == null) || !this.prefix.startsWith("jdbc:")) {
                throw new IllegalStateException("Empty or illegal JDBC URL prefix (" + this.prefix
                        + ") trying to build JDBC driver. The prefix must start with \"jdbc:\"."
                        + "Please consult the driver's manual for the driver-specific JDBC URL syntax.");

            }
            if ((this.mainClass == null)
                    || !this.mainClass.matches("[a-zA-Z]\\w{0,254}(?:.[a-zA-Z]\\w{0,254}){0,10}")) {
                throw new IllegalStateException("Empty or illegal main class (" + this.mainClass
                        + ") trying to build JDBC driver. Please consult the driver's manual,"
                        + " check the correct spelling and provide the main class name via the mainClass() method.");
            }
        }
    }
}