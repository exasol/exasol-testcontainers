package com.exasol.containers.slc;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.slc.fileprovider.FileProvider;
import com.exasol.errorreporting.ExaError;

public class ScriptLanguageContainerInstaller {

    private static final List<String> SUPPORTED_SLC_FILE_EXTENSIONS = List.of(".tar.gz", ".tar.bz2", ".zip");
    private final Bucket bucket;
    private final SlcConfigurator slcConfigurator;
    private final SlcUrlFormatter slcUrlFormatter;

    public ScriptLanguageContainerInstaller(final Bucket bucket, final SlcConfigurator slcConfigurator,
            final SlcUrlFormatter slcUrlFormatter) {
        this.bucket = bucket;
        this.slcConfigurator = slcConfigurator;
        this.slcUrlFormatter = slcUrlFormatter;
    }

    public static ScriptLanguageContainerInstaller create(final ExasolContainer<?> container) {
        return ScriptLanguageContainerInstaller.create(container.createConnection(), container.getDefaultBucket());
    }

    public static ScriptLanguageContainerInstaller create(final Connection connection, final Bucket bucket) {
        return new ScriptLanguageContainerInstaller(bucket, new SlcConfigurator(connection), new SlcUrlFormatter());
    }

    public void install(final ScriptLanguageContainer slc) {
        final FileProvider fileProvider = FileProvider.forSlc(slc);
        validateSlc(slc, fileProvider);
        final Path localPath = fileProvider.getLocalFile();
        final String containerName = localPath.getFileName().toString();
        uploadToBucketFs(localPath, containerName);
        final SlcConfiguration configuration = slcConfigurator.read();
        configuration.setAlias(slc.getAlias(), slcUrlFormatter.format(slc, containerName));
        slcConfigurator.write(configuration);
    }

    private void uploadToBucketFs(final Path file, final String pathInBucket) {
        try {
            bucket.uploadFile(file, pathInBucket);
        } catch (FileNotFoundException | BucketAccessException | TimeoutException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("E-ETC-34")
                    .message("Failed to upload local file file {{file path}} to bucket at {{path in bucket}}.", file,
                            pathInBucket)
                    .toString(), exception);
        }
    }

    private void validateSlc(final ScriptLanguageContainer slc, final FileProvider fileProvider) {
        final String fileName = fileProvider.getFileName();
        if (SUPPORTED_SLC_FILE_EXTENSIONS.stream().noneMatch(fileName::endsWith)) {
            throw new IllegalArgumentException(wrongFileExtensionErrorMessage(slc, fileName));
        }
        if (slc.getLocalFile() != null && !Files.exists(slc.getLocalFile())) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-27")
                    .message("Local file {{local file}} does not exist", slc.getLocalFile()).toString());
        }
        if (slc.getUrl() != null && slc.getSha512sum() == null) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ETC-42")
                    .message("An URL is specified but sha512sum checksum is missing").toString());
        }
    }

    private String wrongFileExtensionErrorMessage(final ScriptLanguageContainer slc, final String fileName) {
        if (slc.getLocalFile() != null) {
            return ExaError.messageBuilder("E-ETC-35")
                    .message("File {{file path}} has an unsupported file extension.", slc.getLocalFile())
                    .mitigation("The following file extensions are supported for SLCs: {{supported file extensions}}.",
                            SUPPORTED_SLC_FILE_EXTENSIONS)
                    .toString();
        } else {
            return ExaError.messageBuilder("E-ETC-40")
                    .message("Filename {{file name}} of URL {{url}} has an unsupported file extension.", fileName,
                            slc.getUrl())
                    .mitigation("The following file extensions are supported for SLCs: {{supported file extensions}}.",
                            SUPPORTED_SLC_FILE_EXTENSIONS)
                    .toString();
        }
    }
}
