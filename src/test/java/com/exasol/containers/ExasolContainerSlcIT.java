package com.exasol.containers;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.*;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.BucketAccessException;

class ExasolContainerSlcIT {

    @Test
    void installSlc() throws FileNotFoundException, BucketAccessException, TimeoutException, SQLException {
        try (final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()) {
            container.withReuse(true).start();
            final Path slc = Path.of("/Users/chp/Downloads/template-Exasol-all-python-3.10_release.tar.gz");
            final String containerName = slc.getFileName().toString();
            container.getDefaultBucket().uploadFile(slc, containerName);
            final String unpackedContainerName = "template-Exasol-all-python-3.10_release";
            final Connection connection = container.createConnection();
            final String alias = "MY_SLC";
            final String language = "python";
            final String udfEntryPoint = "exaudf/exaudfclient_py3";

            final String defaultSlcConfiguration = getDefaultSlcConfiguration(connection);
            final String newConfig = defaultSlcConfiguration + " " + alias + "=localzmq+protobuf:///bfsdefault/default/"
                    + unpackedContainerName + "?lang=" + language + "#buckets/bfsdefault/default/"
                    + unpackedContainerName + "/" + udfEntryPoint;
            connection.createStatement().execute("ALTER SYSTEM SET SCRIPT_LANGUAGES='" + newConfig + "'");
            connection.createStatement().execute("ALTER SESSION SET SCRIPT_LANGUAGES='" + newConfig + "'");

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

    private String getDefaultSlcConfiguration(final Connection connection) throws SQLException {
        final ResultSet rs = connection
                .prepareStatement("SELECT system_value FROM exa_parameters WHERE parameter_name='SCRIPT_LANGUAGES'")
                .executeQuery();
        while (rs.next()) {
            final String currentSlc = rs.getString(1);
            return currentSlc;
        }
        throw new IllegalStateException("No SLC configuration found.");
    }
}
