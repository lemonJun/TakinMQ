package com.bj58.spat.esbclient.communication.detect;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import org.apache.commons.logging.Log;
import java.nio.channels.SocketChannel;
import com.bj58.spat.esbclient.ESBMessage;
import com.bj58.spat.esbclient.ESBSubject;

import org.apache.commons.logging.LogFactory;
import java.util.concurrent.locks.ReentrantLock;
import java.nio.channels.NotYetConnectedException;
import com.bj58.spat.esbclient.communication.Server;
import com.bj58.spat.esbclient.communication.ServerState;
import com.bj58.spat.esbclient.exception.ConnectTimeoutException;

/**
 * 服务重启的探测模块
 * 由于服务端执行重启过程中会有一段时间处于待关闭状态，所以通过简单的ping机制无法判断服务端正常状态
 * 通过发送消息的方式判断服务端状态
 */
public class DetectChannel {

    private static final Log logger = LogFactory.getLog(DetectChannel.class);

    private Server server;
    private SocketChannel sockChannel;
    private final ReentrantLock lock = new ReentrantLock();
    private ByteBuffer receiveBuffer, sendBuffer, receiveMsg;

    protected DetectChannel(Server server) throws IOException, ConnectTimeoutException {
        InetSocketAddress addr = new InetSocketAddress(server.getServerConfig().getIp(), server.getServerConfig().getPort());
        sockChannel = SocketChannel.open();
        sockChannel.configureBlocking(false);
        sockChannel.socket().setReceiveBufferSize(1024);
        sockChannel.socket().setSendBufferSize(1024);
        sockChannel.socket().setTcpNoDelay(true);
        sockChannel.socket().setKeepAlive(true);
        sockChannel.connect(addr);

        long begin = System.currentTimeMillis();
        while (true) {
            if ((System.currentTimeMillis() - begin) > 5000) {
                throw new ConnectTimeoutException("connect to " + addr + " timeout：" + 5000);
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

        receiveBuffer = ByteBuffer.allocateDirect(1024);
        receiveMsg = ByteBuffer.allocate(1024);
        sendBuffer = ByteBuffer.allocateDirect(1024);
        this.server = server;
        logger.info("esb client DetectChannel create a new connection:" + this.toString());
    }

    public int send(byte[] data) throws IOException {
        int count = 0;
        lock.lock();
        try {
            sendBuffer.clear();
            sendBuffer.put(data);
            sendBuffer.put(ESBMessage.DELIMITER);
            sendBuffer.flip();
            while (sendBuffer.hasRemaining()) {
                count = sockChannel.write(sendBuffer);
            }
            return count;
        } finally {
            lock.unlock();
        }
    }

    public void close() throws IOException {
        if (sockChannel != null) {
            logger.warn("DetectChannel close a ESBClient socket: local addr:" + sockChannel.socket().getLocalAddress());
            sockChannel.close();
            sockChannel = null;
        }
    }

    /**
     * 探测监听返回信息
     * @throws IOException
     * @throws InterruptedException
     */
    private volatile int index = 0;

    public int frameHandle() throws Throwable {
        int sagle = 0;
        try {
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

                        //如果返回数据为正常则标记为当前server为正常状态
                        if (msgBuf[5] == 6) {
                            server.connect();
                            server.setState(ServerState.Normal);
                            ESBSubject sujectArray[] = server.getSubjectArray();
                            if (sujectArray != null && sujectArray.length > 0) {
                                server.subscribe(sujectArray);
                            }
                            return 6;
                        } else if (msgBuf[5] == 4) {
                            return 4;
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
                    this.close();
                    return -1;
                } catch (IOException e1) {
                    throw new IOException("close socket error:" + e1);
                }
            }
        } catch (IOException e) {
            if (sockChannel != null) {
                try {
                    sockChannel.close();
                } catch (IOException e1) {
                    logger.error("DetectDataReceiver receive data IOException[Channel close]:", e1);
                    throw new IOException("DetectDataReceiver receive data IOException[Channel close]:" + e1);
                }
            }
            throw new IOException("DetectDataReceiver receive data IOException:" + e);
        } catch (NotYetConnectedException nyce) {
            if (sockChannel != null) {
                try {
                    sockChannel.close();
                } catch (IOException ioe) {
                    logger.error("DetectDataReceiver receive data NotYetConnectedException[Channel close]:", ioe);
                    throw new IOException("DetectDataReceiver receive data NotYetConnectedException[Channel close]:" + ioe);
                }
            }
            throw new NotYetConnectedException();
        } catch (Throwable t) {
            logger.error("frameHandle DetectDataReceiver receive data Throwable", t);
            throw new Throwable("frameHandle DetectDataReceiver receive data Throwable:" + t);
        }
        return sagle;
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
