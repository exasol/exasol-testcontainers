package com.exasol.containers;

import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.*;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.slc.ScriptLanguageContainer;
import com.exasol.containers.slc.ScriptLanguageContainer.Language;

class ExasolContainerSlcIT {

    @Test
    void installSlc() throws FileNotFoundException, BucketAccessException, TimeoutException, SQLException {
        try (final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()) {
            final long timestamp = System.currentTimeMillis();
            final String alias = "MY_SLC_" + timestamp;
            final Path slcFile = Path.of("/Users/chp/Downloads/template-Exasol-all-python-3.10_release.tar.gz");
            final ScriptLanguageContainer slc = ScriptLanguageContainer.builder() //
                    .alias(alias) //
                    .language(Language.PYTHON) //
                    .localFile(slcFile) //
                    .build();
            container.withReuse(true).withScriptLanguageContainer(slc).start();

            try (final Connection connection = container.createConnection();
                    Statement statement = connection.createStatement()) {

                final String schemaName = "TEST_" + timestamp;
                statement.execute("create schema " + schemaName);
                final String udf = "CREATE " + alias + " SCALAR SCRIPT " + schemaName + ".get_python_version()\n" + //
                        "RETURNS VARCHAR(2000) AS\n" + //
                        "import sys\n" + //
                        "def run(ctx):\n" + //
                        "  return f\"{sys.version_info.major}.{sys.version_info.minor}\"\n" + //
                        "/";
                statement.execute(udf);
                assertThat(statement.executeQuery("select " + schemaName + ".get_python_version()"),
                        table().row("3.10").matches());
            }
        }
    }
}
