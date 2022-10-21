package com.exasol.containers.ssh;

import java.io.IOException;
import java.io.InputStream;

class StreamSimulator extends InputStream {
    private int iteration = 0;
    private final String[] output = { "", "first,", "", "second" };

    @Override
    public int read(final byte b[], final int off, final int len) throws IOException {
        final String batch = this.output[this.iteration++];
        final byte[] ba = batch.getBytes(RemoteExecutorTest.CHARSET);
        for (int i = 0; i < batch.length(); i++) {
            b[i] = ba[i];
        }
        return batch.isEmpty() ? -1 : ba.length;
    }

    @Override
    public int available() throws IOException {
        return this.iteration < this.output.length ? 1 : 0;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}