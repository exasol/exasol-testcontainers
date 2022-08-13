package com.exasol.database;

import java.util.Arrays;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class MyTest {

    @Test
    void test() {
        final TimeZone z = TimeZone.getTimeZone("UTC");
        final String s = pattern("/bfsdefault/default/abc/def.txt").replaceAll("([/()])", "\\\\$1");
        System.out.println(System.getenv("DOCKER_HOST"));
        System.out.println(System.getProperty("java.util.logging.config.file"));
    }

    private String pattern(final String pathInBucket) {
        return "removed sync future for id ((" + Stream.concat( //
                Stream.of("bfsdefault", "default"), //
                Arrays.stream(pathInBucket.split("/"))) //
                .map(s -> String.format("'%s'", s)) //
                .collect(Collectors.joining(", ")) + "))";
    }

}
