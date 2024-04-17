package com.exasol.containers.slc;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.util.*;

import com.exasol.errorreporting.ExaError;

class SlcConfiguration {

    private final Map<String, String> entries;

    private SlcConfiguration(final Map<String, String> entries) {
        this.entries = entries;
    }

    static SlcConfiguration parse(final String value) {
        final Map<String, String> entries = Arrays.stream(value.split(" ")) //
                .filter(not(String::isBlank)) //
                .map(SlcConfiguration::parseEntry) //
                .collect(toMap(entry -> entry.get(0), entry -> entry.get(1), (a, b) -> {
                    throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-31")
                            .message("Found two entries with the same key: {{value 1}}, {{value 2}}", a, b).toString());
                }, LinkedHashMap::new));
        return new SlcConfiguration(entries);
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

    String format() {
        return this.entries.entrySet().stream() //
                .map(entry -> entry.getKey() + "=" + entry.getValue()) //
                .collect(joining(" "));
    }

    void setAlias(final String alias, final String configuration) {
        this.entries.put(alias.trim(), configuration.trim());
    }
}
