package com.bj58.spat.esb.server.communication;

import static org.jboss.netty.buffer.ChannelBuffers.directBuffer;
import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;

import com.bj58.spat.esb.server.protocol.ProtocolConst;

/**
 * netty Pipeline Factory
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 * <a href="http://blog.58.com/spat/">blog</a>
 * <a href="http://www.58.com">website</a>
 * 
 */
public class TcpPipelineFactory implements ChannelPipelineFactory {

    private final ChannelHandler handler;
    private int frameMaxLength;

    public TcpPipelineFactory(ChannelHandler handler, int frameMaxLength) {
        this.handler = handler;
        this.frameMaxLength = frameMaxLength;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        ChannelBuffer buf = directBuffer(ProtocolConst.P_END_TAG.length);
        buf.writeBytes(ProtocolConst.P_END_TAG);
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(this.frameMaxLength, true, buf));
        pipeline.addLast("handler", handler);
        return pipeline;
    }
}