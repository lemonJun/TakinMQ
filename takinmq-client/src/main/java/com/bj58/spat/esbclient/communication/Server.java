package com.bj58.spat.esbclient.communication;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.spat.esbclient.ESBMessage;
import com.bj58.spat.esbclient.ESBSubject;
import com.bj58.spat.esbclient.config.ServerConfig;
import com.bj58.spat.esbclient.exception.CommunicationException;
import com.bj58.spat.esbclient.exception.ConnectTimeoutException;
import com.bj58.spat.esbclient.exception.SerializeException;
import com.bj58.spat.esbclient.util.InetAddressUtil;
import com.bj58.spat.esbclient.util.SessionIDGenerator;

public class Server {

    private static final Log logger = LogFactory.getLog(Server.class);

    private ChannelPool channelPool;
    private ServerConfig servConfig;
    private ServerReciveHandler serverrecivehandler;
    private ServerState state = ServerState.Normal;
    private ESBSubject subjectArray[] = null;

    public Server(ServerConfig servConfig, ServerReciveHandler serverrecivehandler) throws IOException {
        this.servConfig = servConfig;
        this.channelPool = new ChannelPool();
        this.serverrecivehandler = serverrecivehandler;
        for (int i = 0; i < servConfig.getInitConn(); i++) {
            try {
                channelPool.add(new NIOChannel(servConfig, this, serverrecivehandler));
            } catch (IOException ex1) {
                logger.error("create sock error in SockPool", ex1);
            } catch (ConnectTimeoutException ex2) {
                logger.error("create sock timeout in SockPool", ex2);
            }
        }

        if (channelPool.count() == 0 && servConfig.getInitConn() > 0) {
            throw new IOException("create sockpool error:" + servConfig.getIp() + ":" + servConfig.getPort());
        }
    }

    public void connect() throws IOException, ConnectTimeoutException {
        if (this.channelPool == null) {
            this.channelPool = new ChannelPool();
        }
        this.channelPool.add(new NIOChannel(servConfig, this, serverrecivehandler));
    }

    public void sendMessage(ESBMessage msg) throws IOException, ConnectTimeoutException, SerializeException, CommunicationException {
        NIOChannel channel = null;
        try {
            channel = channelPool.getChannel();
            msg.setIp(InetAddressUtil.getIpInt());
            msg.setTimestamp(System.currentTimeMillis());
            msg.setSessionID(SessionIDGenerator.getSessionID());
            channel.send(msg.toBytes());
        } catch (IOException e) {
            if (null != channel) {
                destroyChannel(channel);
            }
            e.printStackTrace();
            check();
            throw e;
        } catch (ConnectTimeoutException e) {
            destroyChannel(channel);
            check();
            throw e;
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void sendQueueMessage(ESBMessage msg) throws IOException, ConnectTimeoutException, SerializeException, CommunicationException {
        NIOChannel channel = null;
        try {
            channel = channelPool.getChannel();
            msg.setIp(InetAddressUtil.getIpInt());
            msg.setTimestamp(System.currentTimeMillis());
            msg.setSessionID(SessionIDGenerator.getSessionID());
            channel.send(msg.toBytes());
        } catch (IOException e) {
            logger.info("this message is IOException " + Arrays.toString(msg.toBytes()));
            if (null != channel) {
                destroyChannel(channel);
            }
            check();
            throw e;
        } catch (ConnectTimeoutException e) {
            logger.info("this message is ConnectTimeoutException " + Arrays.toString(msg.toBytes()));
            destroyChannel(channel);
            check();
            throw e;
        } catch (Exception exc) {
            logger.error(exc.getMessage());
        }
    }

    public void subscribe(ESBSubject... subjects) throws IOException, ConnectTimeoutException, SerializeException, CommunicationException {
        NIOChannel channel = null;
        try {
            channel = channelPool.getChannel();
            this.subjectArray = subjects;
            channel.send(ESBSubject.toBytes(subjects));
        } catch (IOException e) {
            if (null != channel) {
                destroyChannel(channel);
            }
            check();
            throw e;
        } catch (ConnectTimeoutException e) {
            destroyChannel(channel);
            check();
            throw e;
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void destroyChannel(NIOChannel channel) {
        channelPool.destroy(channel);
    }

    public void check() {
        DaemonChecker.check(this);
    }

    public ServerConfig getServerConfig() {
        return servConfig;
    }

    public ChannelPool getChannelPool() {
        return channelPool;
    }

    public ServerState getState() {
        return state;
    }

    public void setState(ServerState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return servConfig.getIp() + ":" + servConfig.getPort();
    }

    public void close() {
        channelPool.destroy();
    }

    public ESBSubject[] getSubjectArray() {
        return subjectArray;
    }
}
