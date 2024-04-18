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

/**
 * This class installs a {@link ScriptLanguageContainer} into the Exasol database.
 */
public class ScriptLanguageContainerInstaller {
    private static final List<String> SUPPORTED_SLC_FILE_EXTENSIONS = List.of(".tar.gz", ".tar.bz2", ".zip");
    private final Bucket bucket;
    private final SlcConfigurator slcConfigurator;
    private final SlcUrlFormatter slcUrlFormatter;

    ScriptLanguageContainerInstaller(final Bucket bucket, final SlcConfigurator slcConfigurator,
            final SlcUrlFormatter slcUrlFormatter) {
        this.bucket = bucket;
        this.slcConfigurator = slcConfigurator;
        this.slcUrlFormatter = slcUrlFormatter;
    }

    /**
     * Create a new {@link ScriptLanguageContainerInstaller} for a given {@link ExasolContainer}.
     * 
     * @param container Exasol container
     * @return new {@link ScriptLanguageContainerInstaller}
     */
    public static ScriptLanguageContainerInstaller create(final ExasolContainer<?> container) {
        return ScriptLanguageContainerInstaller.create(container.createConnection(), container.getDefaultBucket());
    }

    /**
     * Create a new {@link ScriptLanguageContainerInstaller} for a given {@link Connection} and {@link Bucket}.
     * 
     * @param connection connection to an Exasol database
     * @param bucket     bucket to upload the SLC to
     * @return new {@link ScriptLanguageContainerInstaller}
     */
    public static ScriptLanguageContainerInstaller create(final Connection connection, final Bucket bucket) {
        return new ScriptLanguageContainerInstaller(bucket, new SlcConfigurator(connection), new SlcUrlFormatter());
    }

    /**
     * Install a {@link ScriptLanguageContainer} into the Exasol database.
     * <p>
     * This will perform the following steps:
     * <ol>
     * <li>Validate the given SLC configuration</li>
     * <li>Download the SLC if necessary</li>
     * <li>Upload the SLC to the bucket filesystem</li>
     * <li>Update the SLC configuration in the Exasol database</li>
     * </ol>
     * 
     * @param slc script language container to install
     */
    // [impl->dsn~install-custom-slc~1]
    public void install(final ScriptLanguageContainer slc) {
        final FileProvider fileProvider = FileProvider.forSlc(slc);
        validateSlc(slc, fileProvider);
        final Path localFile = fileProvider.getLocalFile();
        final String fileName = localFile.getFileName().toString();
        uploadToBucketFs(localFile, fileName);
        updateSlcConfiguration(slc, fileName);
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

    private void updateSlcConfiguration(final ScriptLanguageContainer slc, final String fileName) {
        final SlcConfiguration configuration = slcConfigurator.read();
        configuration.setAlias(slc.getAlias(), slcUrlFormatter.format(slc, removeExtension(slc, fileName)));
        slcConfigurator.write(configuration);
    }

    private String removeExtension(final ScriptLanguageContainer slc, final String fileName) {
        final String extension = SUPPORTED_SLC_FILE_EXTENSIONS.stream().filter(fileName::endsWith).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(wrongFileExtensionErrorMessage(slc, fileName)));
        return fileName.substring(0, fileName.length() - extension.length());
    }
}
