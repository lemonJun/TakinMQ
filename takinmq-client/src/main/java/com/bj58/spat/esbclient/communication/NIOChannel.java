package com.bj58.spat.esbclient.communication;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import org.apache.commons.logging.Log;
import java.nio.channels.SocketChannel;
import com.bj58.spat.esbclient.ESBMessage;
import org.apache.commons.logging.LogFactory;
import com.bj58.spat.esbclient.config.ESBConfig;
import java.util.concurrent.locks.ReentrantLock;
import com.bj58.spat.esbclient.config.ServerConfig;
import com.bj58.spat.esbclient.exception.SerializeException;
import com.bj58.spat.esbclient.exception.CommunicationException;
import com.bj58.spat.esbclient.exception.ConnectTimeoutException;
import com.bj58.spat.esbclient.communication.detect.ServerStateDetect;

public class NIOChannel {

    private static final Log logger = LogFactory.getLog(NIOChannel.class);

    private Server server;
    private boolean isOpen = false;
    private SocketChannel sockChannel;
    private final ReentrantLock lock = new ReentrantLock();
    private ServerReciveHandler serverrecivehandler = null;
    private ByteBuffer receiveBuffer, sendBuffer, receiveMsg;

    protected NIOChannel(ServerConfig servConf, Server server, ServerReciveHandler serverrecivehandler) throws IOException, ConnectTimeoutException {
        InetSocketAddress addr = new InetSocketAddress(servConf.getIp(), servConf.getPort());
        sockChannel = SocketChannel.open();
        sockChannel.configureBlocking(false);
        sockChannel.socket().setReceiveBufferSize(servConf.getRecvBufferSize());
        sockChannel.socket().setSendBufferSize(servConf.getSendBufferSize());
        sockChannel.socket().setTcpNoDelay(!servConf.isNagle());
        sockChannel.socket().setKeepAlive(servConf.isKeepAlive());
        sockChannel.connect(addr);

        long begin = System.currentTimeMillis();
        while (true) {

            if ((System.currentTimeMillis() - begin) > servConf.getConnectTimeOut()) {
                throw new ConnectTimeoutException("connect to " + addr + " timeout：" + servConf.getConnectTimeOut());
            }
            sockChannel.finishConnect();
            if (sockChannel.isConnected()) {
                break;
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }
        }

        receiveBuffer = ByteBuffer.allocateDirect(servConf.getMaxPakageSize());
        receiveMsg = ByteBuffer.allocate(servConf.getMaxPakageSize());
        sendBuffer = ByteBuffer.allocateDirect(servConf.getRecvBufferSize());
        this.isOpen = true;
        this.server = server;
        this.serverrecivehandler = serverrecivehandler;
        DataReceiver.getInstance().regChannel(this);
        logger.info("esb client create a new connection:" + this.toString());
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean isOpen() {
        return this.isOpen && this.sockChannel.isOpen();
    }

    public int send(byte[] data) throws CommunicationException, IOException {
        int count = 0;
        lock.lock();
        try {
            sendBuffer.clear();
            sendBuffer.put(data);
            sendBuffer.put(ESBMessage.DELIMITER);
            sendBuffer.flip();

            int retryCount = 0;
            while (sendBuffer.hasRemaining()) {
                count += sockChannel.write(sendBuffer);
                if (retryCount++ > ESBConfig.SEND_RETRY_COUNT) {
                    throw new CommunicationException("retry write count(" + retryCount + ") above SEND_RETRY_COUNT");
                }
            }
            return count;
        } finally {
            lock.unlock();
        }
    }

    public void closeAndDestroyChannelPool() throws IOException {
        isOpen = false;
        if (sockChannel != null) {
            logger.warn("close a ESBClient socket: local addr:" + sockChannel.socket().getLocalAddress());
            sockChannel.close();
            server.getChannelPool().destroy(this);
            sockChannel = null;
        }
    }

    public void close() throws IOException {
        isOpen = false;
        if (sockChannel != null) {
            logger.warn("close a ESBClient socket: local addr:" + sockChannel.socket().getLocalAddress());
            sockChannel.close();
            sockChannel = null;
        }
    }

    /**
     * 监听返回信息
     * @throws IOException
     * @throws InterruptedException
     */
    private volatile int index = 0;

    public void frameHandle() throws IOException, SerializeException, InterruptedException {

        receiveBuffer.clear();
        int ret = sockChannel.read(receiveBuffer);
        receiveBuffer.flip();

        while (receiveBuffer.remaining() > 0) {
            byte b = receiveBuffer.get();
            receiveMsg.put(b);
            if (b == ESBMessage.DELIMITER[index]) {
                index++;
                if (index == ESBMessage.DELIMITER.length) {
                    index = 0;
                    receiveMsg.flip();
                    byte[] msgBuf = new byte[receiveMsg.remaining() - ESBMessage.DELIMITER.length];
                    receiveMsg.get(msgBuf);
                    receiveMsg.clear();

                    /**
                     * 服务端推送数据至订阅者
                     */
                    if (msgBuf[5] == 2) {
                        serverrecivehandler.offer(msgBuf);
                    } else if (msgBuf[5] == 4) { //服务器reboot时返回客户端
                        server.setState(ServerState.ReStart);
                        ServerStateDetect.getInstance().check(server);
                    }
                    continue;
                }
            } else if (index != 0) {
                if (b == ESBMessage.DELIMITER[0]) {
                    index = 1;
                } else {
                    index = 0;
                }
            }
        }

        if (ret < 0) {
            try {
                //this.close();
                this.closeAndDestroyChannelPool();
            } catch (IOException e1) {
                logger.error("close socket error", e1);
            }
            //this.getServer().check();
            ServerStateDetect.getInstance().check(server);
            logger.error("server is close,client will close!");
        }
    }

    public SocketChannel getSockChannel() {
        return sockChannel;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public String toString() {
        try {
            return (sockChannel == null) ? "" : sockChannel.socket().toString();
        } catch (Throwable ex) {
            return "socket[error:" + ex.getMessage() + "]";
        }
    }
}
