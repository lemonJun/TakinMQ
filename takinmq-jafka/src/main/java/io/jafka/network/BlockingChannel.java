package io.jafka.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.ReentrantLock;

import io.jafka.common.ErrorMapping;
import io.jafka.common.annotations.NotThreadSafe;
import io.jafka.utils.Closer;
import io.jafka.utils.KV;

/**
 * A simple blocking channel with timeouts correctly enabled.
 *
 * @author adyliu (imxylz@gmail.com)
 * @since 1.3
 */
@NotThreadSafe
public class BlockingChannel {

    public static final int DEFAULT_BUFFER_SIZE = -1;
    private final String host;
    private final int port;
    private final int readBufferSize;
    private final int writeBufferSize;
    private final int readTimeoutMs;

    private boolean connected = false;
    private SocketChannel channel;
    private ReadableByteChannel readChannel;
    private GatheringByteChannel writeChannel;
    private final ReentrantLock lock = new ReentrantLock();

    public BlockingChannel(String host, int port, int readBufferSize, int writeBufferSize, int readTimeoutMs) {
        this.host = host;
        this.port = port;
        this.readBufferSize = readBufferSize;
        this.writeBufferSize = writeBufferSize;
        this.readTimeoutMs = readTimeoutMs;
    }

    public void connect() throws IOException {
        lock.lock();
        try {
            if (!connected) {
                channel = SocketChannel.open();
                if (readBufferSize > 0) {
                    channel.socket().setReceiveBufferSize(readBufferSize);
                }
                if (writeBufferSize > 0) {
                    channel.socket().setSendBufferSize(writeBufferSize);
                }
                channel.configureBlocking(true);
                channel.socket().setSoTimeout(readTimeoutMs);
                channel.socket().setKeepAlive(true);
                channel.socket().setTcpNoDelay(true);
                channel.connect(new InetSocketAddress(host, port));

                writeChannel = channel;
                readChannel = Channels.newChannel(channel.socket().getInputStream());
                connected = true;
            }
        } finally {
            lock.unlock();
        }
    }

    public void disconnect() {
        lock.lock();
        try {
            if (connected || channel != null) {
                Closer.closeQuietly(channel);
                Closer.closeQuietly(channel.socket());
                Closer.closeQuietly(readChannel);
                channel = null;
                readChannel = null;
                writeChannel = null;
                connected = false;
            }

        } finally {
            lock.unlock();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public int send(Request request) throws IOException {
        if (!isConnected()) {
            throw new ClosedChannelException();
        }
        return new BoundedByteBufferSend(request).writeCompletely(writeChannel);
    }

    public KV<Receive, ErrorMapping> receive() throws IOException {
        BoundedByteBufferReceive response = new BoundedByteBufferReceive();
        response.readCompletely(readChannel);
        return new KV<Receive, ErrorMapping>(response, ErrorMapping.valueOf(response.buffer().getShort()));
    }
}
