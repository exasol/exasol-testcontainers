package com.exasol.containers.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;

/**
 * {@link AutoCloseable} for SSH channel
 */
public class SshConnection implements AutoCloseable {

    private final Channel channel;

    /**
     * @param channel SSH channel to connect
     * @throws JSchException if connect fails
     */
    public SshConnection(final Channel channel) throws JSchException {
        this.channel = channel;
        channel.connect();
    }

    @Override
    public void close() {
        this.channel.disconnect();
    }

    /**
     * @return channel used by current {@link SshConnection}
     */
    public Channel channel() {
        return this.channel;
    }
}
