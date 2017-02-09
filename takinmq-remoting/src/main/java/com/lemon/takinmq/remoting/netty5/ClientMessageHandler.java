package com.lemon.takinmq.remoting.netty5;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

public class ClientMessageHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(ClientMessageHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RemotingMessage message = (RemotingMessage) msg;
        logger.info(message.getResultJson());

        final ResponseFuture responseFuture = RemotingNettyClient.responseTable.get(message.getOpaque());

        if (responseFuture != null) {
            System.out.println("response is not null");
            responseFuture.putResponse(message);
        }
        RemotingNettyClient.responseTable.remove(message.getOpaque());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
