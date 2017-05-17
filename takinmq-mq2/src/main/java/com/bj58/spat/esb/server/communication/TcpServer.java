package com.bj58.spat.esb.server.communication;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import sun.misc.Signal;

import com.bj58.spat.esb.server.bootstrap.signal.RebootSignalHandle;
import com.bj58.spat.esb.server.communication.telnet.TelnetServer;
import com.bj58.spat.esb.server.config.ServerConfigBase;
import com.bj58.spat.esb.server.daemon.ContextDispatcher;

public class TcpServer implements IServer {

    private static final Log logger = LogFactory.getLog(TcpHandler.class);
    private static final ServerBootstrap bootstrap = new ServerBootstrap();
    public static final ChannelGroup allChannels = new DefaultChannelGroup("ESB");
    private static TcpServer tcpServer = new TcpServer();

    public static TcpServer getInstance() {
        return tcpServer;
    }

    @Override
    public void start() throws Exception {
        final boolean tcpNoDelay = true;
        bootstrap.setFactory(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(), ServerConfigBase.getWorkerCount()));
        bootstrap.setPipelineFactory(new TcpPipelineFactory(new TcpHandler(), ServerConfigBase.getFrameMaxLength()));
        bootstrap.setOption("child.tcpNoDelay", tcpNoDelay);
        bootstrap.setOption("child.receiveBufferSize", ServerConfigBase.getReceiveBufferSize());
        bootstrap.setOption("child.sendBufferSize", ServerConfigBase.getSendBufferSize());

        ContextDispatcher.start();

        Class.forName("com.bj58.spat.esb.server.util.MessageCounter");//该类提供服务前提前初始化，加载数据库的counter数据

        try {
            InetSocketAddress socketAddress = null;
            socketAddress = new InetSocketAddress(ServerConfigBase.getServerListenIP(), ServerConfigBase.getServerListenPort());
            Channel channel = bootstrap.bind(socketAddress);
            allChannels.add(channel);
        } catch (Exception e) {
            logger.error("init socket server error", e);
            System.exit(1);
        }

        logger.info("------------------signal registr start---------------------");
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName != null && osName.indexOf("window") == -1) {
            RebootSignalHandle rebootHandler = new RebootSignalHandle();
            Signal sig = new Signal("USR2");
            Signal.handle(sig, rebootHandler);
        }
        logger.info("------------------signal registr success----------------------\n");
    }

    @Override
    public void stop() throws Exception {
        logger.info("----------------------------------------------------");
        logger.info("-- socket server closing...");
        logger.info("-- channels count : " + allChannels.size());
        ChannelGroupFuture future = allChannels.close();
        logger.info("-- closing all channels...");
        future.awaitUninterruptibly();
        logger.info("-- closed all channels...");
        bootstrap.getFactory().releaseExternalResources();
        logger.info("-- released external resources");
        logger.info("-- close success !");
        logger.info("----------------------------------------------------");
        TelnetServer.getInstance().stop();

    }
}
