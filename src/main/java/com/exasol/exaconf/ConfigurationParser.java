package com.exasol.exaconf;

import java.util.*;

import com.exasol.config.ClusterConfiguration;

/**
 * Parser for Exasol cluster configuration files ({@code EXAConf}.
 */
public class ConfigurationParser {
    private String section = "";
    private String subsection = "";
    private final Map<String, String> parameters = new HashMap<>();
    private final String rawConfig;
    private static final Set<String> BASE64_ENCODED_VALUES = Set.of("ReadPasswd", "WritePasswd");

    /**
     * Create a new instance of a {@link ConfigurationParser}.
     *
     * @param rawConfig configuration content as text
     */
    public ConfigurationParser(final String rawConfig) {
        this.rawConfig = rawConfig;
    }

    /**
     * Parse a configuration file.
     *
     * @return Configuration as object
     */
    public ClusterConfiguration parse() {
        try (final Scanner scanner = new Scanner(this.rawConfig)) {
            while (scanner.hasNext()) {
                parseLine(scanner.nextLine());
            }
        }
        return new ClusterConfiguration(this.parameters);
    }

    private void parseLine(final String line) {
        final String unindentedLine = line.stripLeading();
        parseUnindentedLine(unindentedLine);
    }

    private void parseUnindentedLine(final String line) {
        if (line.isBlank() || line.startsWith("#")) {
            // intentionally empty
        } else if (line.startsWith("[[")) {
            this.subsection = stripSectionName(line) + "/";
        } else if (line.startsWith("[")) {
            this.section = stripSectionName(line) + "/";
            this.subsection = "";
        } else {
            final int assignmentOperatorPosition = line.indexOf('=');
            if (assignmentOperatorPosition > 0) {
                final String key = line.substring(0, assignmentOperatorPosition - 1).trim();
                final String value = line.substring(assignmentOperatorPosition + 1).trim();
                final String decodedValue = (BASE64_ENCODED_VALUES.contains(key) ? decodePassword(value) : value);
                this.parameters.put(this.section + this.subsection + key, decodedValue);
            }
        }
    }

    private String stripSectionName(final String line) {
        return line.replaceAll("[\\s\\[\\]]", "");
    }

    private String decodePassword(final String value) {
        return new String(Base64.getDecoder().decode(value));
    }
}