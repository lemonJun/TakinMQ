package com.lemon.takinmq.remoting.codec;

import com.alibaba.fastjson.JSON;
import com.lemon.takinmq.remoting.netty5.RemotingMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JsonEncode extends MessageToByteEncoder<RemotingMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingMessage msg, ByteBuf out) throws Exception {
        byte[] body = JSON.toJSONString(msg).getBytes();//将对象转换为byte
        int dataLength = body.length; //读取消息的长度
        out.writeInt(dataLength); //先将消息长度写入，也就是消息头
        out.writeBytes(body);
    }
    
}
