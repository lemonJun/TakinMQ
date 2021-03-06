/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jafka.consumer;

import io.jafka.api.FetchRequest;
import io.jafka.common.ErrorMapping;
import io.jafka.common.annotations.ClientSide;
import io.jafka.common.annotations.ThreadSafe;
import io.jafka.message.ByteBufferMessageSet;
import io.jafka.network.BlockingChannel;
import io.jafka.network.Receive;
import io.jafka.network.Request;
import io.jafka.utils.KV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Simple operation with jafka broker
 *
 * @author adyliu (imxylz@gmail.com)
 * @since 1.2
 */
@ThreadSafe
@ClientSide
public class SimpleOperation implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(SimpleOperation.class);

    ////////////////////////////////
    private final BlockingChannel blockingChannel;

    private final Object lock = new Object();

    public SimpleOperation(String host, int port) {
        this(host, port, 30 * 1000, 64 * 1024);
    }

    public SimpleOperation(String host, int port, int soTimeout, int bufferSize) {
        blockingChannel = new BlockingChannel(host, port, bufferSize, BlockingChannel.DEFAULT_BUFFER_SIZE, soTimeout);
    }

    private BlockingChannel connect() throws IOException {
        close();
        blockingChannel.connect();
        return blockingChannel;
    }

    private void disconnect() {
        if (blockingChannel.isConnected()) {
            blockingChannel.disconnect();
        }
    }

    public void close() {
        synchronized (lock) {
            blockingChannel.disconnect();
        }
    }

    private void reconnect() throws IOException {
        disconnect();
        connect();
    }

    private void getOrMakeConnection() throws IOException {
        if (!blockingChannel.isConnected()) {
            connect();
        }
    }

    public KV<Receive, ErrorMapping> send(Request request) throws IOException {
        synchronized (lock) {
            getOrMakeConnection();
            try {
                blockingChannel.send(request);
                return blockingChannel.receive();
            } catch (ClosedByInterruptException cbie) {
                logger.info("receive interrupted");
                throw cbie;
            } catch (IOException e) {
                logger.info("Reconnect in fetch request due to socket error: {}", e.getMessage());
                //retry once
                try {
                    reconnect();
                    blockingChannel.send(request);
                    return blockingChannel.receive();
                } catch (SocketTimeoutException ste) {
                    logger.error("Reconnect fail and recur now: {}", ste.getMessage());
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                    return send(request);
                } catch (ConnectException ce) {
                    logger.error("Reconnect fail and recur now: {}", ce.getMessage());
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                    return send(request);
                } catch (IOException e2) {
                    throw e2;
                }
            }
        }
    }

    public ByteBufferMessageSet fetch(FetchRequest request) throws IOException {
        KV<Receive, ErrorMapping> response = send(request);
        return new ByteBufferMessageSet(response.k.buffer(), request.offset, response.v);
    }

}
