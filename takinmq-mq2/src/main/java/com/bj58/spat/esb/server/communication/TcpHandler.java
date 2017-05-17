package com.bj58.spat.esb.server.communication;

import java.nio.ByteBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.bj58.spat.esb.server.bootstrap.constant.ServerState;
import com.bj58.spat.esb.server.daemon.ContextDispatcher;
import com.bj58.spat.esb.server.daemon.LogWorker;
import com.bj58.spat.esb.server.util.ClientChannelListHelper;

public class TcpHandler extends SimpleChannelUpstreamHandler {

	private static final Log logger = LogFactory.getLog(TcpHandler.class);
	
	private static LogWorker logworker = LogWorker.logworker ;
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		ByteBuffer buffer = ((ChannelBuffer) e.getMessage()).toByteBuffer();
		byte[] reciveByte = buffer.array();	
		Channel channel = e.getChannel();
		ContextDispatcher.dispatch(new ESBContext(channel, reciveByte , System.nanoTime()));
		//记录日志
		logworker.offer(new Object[]{reciveByte,channel});
	}

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		if (e instanceof ChannelStateEvent) {
			logger.debug(e.toString());
		}
		super.handleUpstream(ctx, e);
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
		if(ServerState.isRebooting()){
			e.getChannel().close();
			logger.warn("this server will reboot "+e.getChannel()+" is close");
		}else{
			TcpServer.allChannels.add(e.getChannel());
			logger.info("new channel open:" + e.getChannel().getRemoteAddress().toString());
		}
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.error("unexpected exception from downstream remoteAddress("
				+ e.getChannel().getRemoteAddress().toString() + ")",
				e.getCause());
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
		logger.info("channel is closed:" + e.getChannel().getRemoteAddress().toString());
		ClientChannelListHelper.removeChannelListWithClose(e.getChannel());
		TcpServer.allChannels.remove(e.getChannel());
	}
}
