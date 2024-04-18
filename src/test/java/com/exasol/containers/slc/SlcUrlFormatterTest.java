package com.exasol.containers.slc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import com.exasol.containers.slc.ScriptLanguageContainer.Language;

class SlcUrlFormatterTest {

    @Test
    void formatPython() {
        assertThat(format(ScriptLanguageContainer.builder().language(Language.PYTHON), "container"), equalTo(
                "localzmq+protobuf:///bfsdefault/default/container?lang=python#buckets/bfsdefault/default/container/exaudf/exaudfclient_py3"));
    }

    @Test
    void formatJava() {
        assertThat(format(ScriptLanguageContainer.builder().language(Language.JAVA), "container"), equalTo(
                "localzmq+protobuf:///bfsdefault/default/container?lang=java#buckets/bfsdefault/default/container/exaudf/exaudfclient"));
    }

    @Test
    void formatCustomEntryPoint() {
        assertThat(
                format(ScriptLanguageContainer.builder().language(Language.R).udfEntryPoint("/path/to/entry"),
                        "container"),
                equalTo("localzmq+protobuf:///bfsdefault/default/container?lang=r#buckets/bfsdefault/default/container/path/to/entry"));
    }

    private String format(final ScriptLanguageContainer.Builder slcBuilder, final String containerName) {
        return new SlcUrlFormatter().format(slcBuilder.build(), containerName);
    }
}
