package com.exasol.containers.slc;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.util.*;
import java.util.stream.Collector;

import com.exasol.errorreporting.ExaError;

/**
 * This class represents the configuration of a Script Language Container (SLC) in an Exasol database with
 * system/session parameter {@code SCRIPT_LANGUAGES}. See <a href=
 * "https://docs.exasol.com/db/latest/database_concepts/udf_scripts/adding_new_packages_script_languages.htm">Exasol
 * documentation</a> for details about the syntax.
 */
class SlcConfiguration {
    private final Map<String, String> entries;

    private SlcConfiguration(final Map<String, String> entries) {
        this.entries = entries;
    }

    /**
     * Parse a SLC configuration from a string in format {@code alias1=value1 alias2=value2 ...}.
     * 
     * @param value the configuration string
     * @return parsed configuration
     */
    static SlcConfiguration parse(final String value) {
        final Map<String, String> entries = Arrays.stream(value.split(" ")) //
                .filter(not(String::isBlank)) //
                .map(SlcConfiguration::parseEntry) //
                .collect(toLinkedHashMap());
        return new SlcConfiguration(entries);
    }

    private static Collector<List<String>, ?, LinkedHashMap<String, String>> toLinkedHashMap() {
        return toMap(entry -> entry.get(0), entry -> entry.get(1), SlcConfiguration::failForDuplicateKeys,
                LinkedHashMap::new);
    }

    private static String failForDuplicateKeys(final String a, final String b) {
        throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-31")
                .message("Found two entries with the same key: {{value 1}}, {{value 2}}", a, b).toString());
    }

    private static List<String> parseEntry(final String entry) {
        final int index = entry.indexOf("=");
        if (index == -1) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-28")
                    .message("Invalid entry in SLC configuration: {{entry}}", entry).toString());
        }
        final String key = entry.substring(0, index).trim();
        if (key.isEmpty()) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-29")
                    .message("Invalid key in SLC configuration: {{entry}}", entry).toString());
        }
        final String value = entry.substring(index + 1).trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-30")
                    .message("Invalid value in SLC configuration: {{entry}}", entry).toString());
        }
        return List.of(key, value);
    }

    /**
     * Format the configuration as a string in format {@code alias1=value1 alias2=value2 ...}.
     * 
     * @return formatted configuration
     */
    String format() {
        return this.entries.entrySet().stream() //
                .map(entry -> entry.getKey() + "=" + entry.getValue()) //
                .collect(joining(" "));
    }

    /**
     * Set an alias to a configuration. If the alias already exists, it will be overwritten.
     * 
     * @param alias         alias
     * @param configuration configuration
     */
    void setAlias(final String alias, final String configuration) {
        this.entries.put(alias.trim(), configuration.trim());
    }
}
