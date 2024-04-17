package com.exasol.containers.slc;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.sql.*;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.UncheckedSqlException;
import com.exasol.errorreporting.ExaError;

public class ScriptLanguageContainerInstaller {

    private static final List<String> SUPPORTED_SLC_FILE_EXTENSIONS = List.of(".tar.gz", ".tar.bz2", ".zip");
    private final Connection connection;
    private final Bucket bucket;

    public ScriptLanguageContainerInstaller(final Connection connection, final Bucket bucket) {
        this.connection = connection;
        this.bucket = bucket;
    }

    public static ScriptLanguageContainerInstaller create(final ExasolContainer<?> container) {
        return ScriptLanguageContainerInstaller.create(container.createConnection(), container.getDefaultBucket());
    }

    public static ScriptLanguageContainerInstaller create(final Connection connection, final Bucket bucket) {
        return new ScriptLanguageContainerInstaller(connection, bucket);
    }

    public void install(final ScriptLanguageContainer slc) {
        validateSlc(slc);
        final String containerName = slc.getLocalFile().getFileName().toString();
        try {
            bucket.uploadFile(slc.getLocalFile(), containerName);
        } catch (FileNotFoundException | BucketAccessException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final String unpackedContainerName = "template-Exasol-all-python-3.10_release";
        final String language = "python";

        try {
            final String defaultSlcConfiguration = getScriptLanguagesSystemValue();
            System.out.println("Default slc configuration: " + defaultSlcConfiguration);
            final String newConfig = defaultSlcConfiguration + " " + slc.getAlias()
                    + "=localzmq+protobuf:///bfsdefault/default/" + unpackedContainerName + "?lang="
                    + slc.getLanguage().getName() + "#buckets/bfsdefault/default/" + unpackedContainerName + "/"
                    + slc.getUdfEntryPoint();
            connection.createStatement().execute("ALTER SYSTEM SET SCRIPT_LANGUAGES='" + newConfig + "'");
            connection.createStatement().execute("ALTER SESSION SET SCRIPT_LANGUAGES='" + newConfig + "'");
        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getScriptLanguagesSystemValue() {
        final String query = "SELECT system_value FROM exa_parameters WHERE parameter_name='SCRIPT_LANGUAGES'";
        try (final PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                return rs.getString(1);
            }
            throw new IllegalStateException("No SLC configuration found.");
        } catch (final SQLException exception) {
            throw new UncheckedSqlException("Failed to retrieve default SLC configuration", exception);
        }
    }

    private void validateSlc(final ScriptLanguageContainer slc) {
        final String fileName = slc.getLocalFile().getFileName().toString();
        if (SUPPORTED_SLC_FILE_EXTENSIONS.stream().noneMatch(fileName::endsWith)) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-28")
                    .message("File {{file name}} has an unsupported file extension.", fileName)
                    .mitigation("The following file extensions are supported for SLCs: {{supported file extensions}}.",
                            SUPPORTED_SLC_FILE_EXTENSIONS)
                    .toString());
        }
        if (!Files.exists(slc.getLocalFile())) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-27")
                    .message("Local file {{local file}} does not exist", slc.getLocalFile()).toString());
        }
    }
}
