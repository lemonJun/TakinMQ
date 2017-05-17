package com.bj58.spat.esb.server.communication.telnet;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.apache.commons.logging.Log;
import org.jboss.netty.channel.Channel;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.group.ChannelGroup;
import com.bj58.spat.esb.server.communication.IServer;
import com.bj58.spat.esb.server.config.ServerConfigBase;
import com.bj58.spat.esb.server.config.ServiceConfig;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class TelnetServer implements IServer{
	private static final Log logger = LogFactory.getLog(TelnetServer.class);
	private static final ServerBootstrap bootstrap = new ServerBootstrap();
	public static final ChannelGroup allChannels = new DefaultChannelGroup("ESBControl");
	private static TelnetServer telnetServer = new TelnetServer();
	
	private TelnetServer (){
		
	}
	
	public static TelnetServer getInstance(){
		return telnetServer ;
	}
	
	@Override
	public void start() throws Exception{		
        bootstrap.setFactory(new NioServerSocketChannelFactory(
			                        	Executors.newCachedThreadPool(),
			                        	Executors.newCachedThreadPool()));  
        bootstrap.setPipelineFactory(new TelnetPipelineFactory(new TelnetHandler(), ServerConfigBase.getFrameMaxLength()));
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.receiveBufferSize", ServerConfigBase.getReceiveBufferSize());
        bootstrap.setOption("child.sendBufferSize", ServerConfigBase.getSendBufferSize());
        try {
        	InetSocketAddress socketAddress = null;
        	String telnetListenIP = ServiceConfig.getInstrance().getString("esb.server.telnet.listenIP");
        	int telnetListenPort =  ServiceConfig.getInstrance().getInt("esb.server.telnet.listenPort");
        	
        	socketAddress = new InetSocketAddress(telnetListenIP, telnetListenPort);
            Channel channel = bootstrap.bind(socketAddress);
            allChannels.add(channel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		logger.info("----------------------------------------------------");
		logger.info("-- telnet Server closing...");
		logger.info("-- channels count : " + allChannels.size());
		ChannelGroupFuture future = allChannels.close();
		future.awaitUninterruptibly();
		bootstrap.getFactory().releaseExternalResources();
		logger.info("-- close success !");
		logger.info("----------------------------------------------------");
	}
}
