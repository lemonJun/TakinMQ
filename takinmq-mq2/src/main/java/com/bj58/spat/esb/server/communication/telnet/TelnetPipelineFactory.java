package com.bj58.spat.esb.server.communication.telnet;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;

public class TelnetPipelineFactory implements ChannelPipelineFactory {
	
	private final ChannelHandler handler;
	private int frameMaxLength;

	public TelnetPipelineFactory(ChannelHandler handler, int frameMaxLength) {
		this.handler = handler;
		this.frameMaxLength = frameMaxLength;
	}

	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(frameMaxLength, Delimiters.lineDelimiter()));
		pipeline.addLast("handler", handler);
		return pipeline;
	}
}