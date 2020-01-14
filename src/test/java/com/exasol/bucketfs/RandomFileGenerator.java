package com.exasol.bucketfs;

import java.io.*;
import java.nio.file.Path;
import java.util.Random;

public final class RandomFileGenerator {
    public void createRandomFile(final Path file, final int sizeInKiB) throws FileNotFoundException, IOException {
        try (final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file.toFile()))) {
            writeRandomBytesToStream(stream, sizeInKiB);
        }
    }

    public void writeRandomBytesToStream(final OutputStream stream, final int sizeInKiB) throws IOException {
        final Random random = new Random();
        for (int i = 0; i < sizeInKiB; ++i) {
            final byte bytes[] = new byte[1024];
            random.nextBytes(bytes);
            stream.write(bytes);
        }
    }
}