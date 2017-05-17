package com.bj58.spat.esb.server.communication;

import org.jboss.netty.channel.Channel;

public class ESBContext {

    private Channel channel;
    private byte[] buf;
    private long inTime;

    public ESBContext(Channel channel, byte[] buf, long inTime) {
        super();
        this.channel = channel;
        this.buf = buf;
        this.inTime = inTime;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public byte[] getBuf() {
        return buf;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    public long getInTime() {
        return inTime;
    }

}
