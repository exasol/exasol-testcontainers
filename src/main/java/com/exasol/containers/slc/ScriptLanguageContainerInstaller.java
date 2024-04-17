package com.exasol.containers.slc;

import java.nio.file.Files;
import java.sql.Connection;

import com.exasol.bucketfs.Bucket;
import com.exasol.containers.ExasolContainer;
import com.exasol.errorreporting.ExaError;

public class ScriptLanguageContainerInstaller {

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
        if (!Files.exists(slc.getLocalFile())) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-27")
                    .message("Local file {{local file}} does not exist", slc.getLocalFile()).toString());
        }
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'install'");
    }
}
