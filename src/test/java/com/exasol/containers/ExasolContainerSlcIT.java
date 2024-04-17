package com.exasol.containers;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.slc.ScriptLanguageContainer;
import com.exasol.containers.slc.ScriptLanguageContainer.Language;

class ExasolContainerSlcIT {

    @Test
    void installSlc() throws FileNotFoundException, BucketAccessException, TimeoutException, SQLException {
        try (final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()) {
            final String alias = "MY_SLC";
            final Path slc = Path.of("/Users/chp/Downloads/template-Exasol-all-python-3.10_release.tar.gz");
            container.withReuse(true)
                    .withScriptLanguageContainer(ScriptLanguageContainer.builder().alias(alias)
                            .language(Language.PYTHON).udfEntryPoint("/exaudf/exaudfclient_py3").localFile(slc).build())
                    .start();

            final Connection connection = container.createConnection();
            final String schemaName = "TEST";
            connection.createStatement().execute("create schema " + schemaName);
            final String udf = "CREATE " + alias + " SCALAR SCRIPT " + schemaName
                    + ".my_python_udf(input_parameter VARCHAR(2000))\n" + //
                    "RETURNS VARCHAR(2000) AS\n" + //
                    "def run(ctx):\n" + //
                    "  return ctx.input_parameter\n" + //
                    "/";
            connection.createStatement().execute(udf);
            connection.createStatement().executeQuery("select test.my_python_udf('abc')");
        }
    }
}
