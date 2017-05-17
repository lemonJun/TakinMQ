package com.bj58.esb.server.store;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * 消息集合
 * 
 * 
 */
public interface MessageSet {

    public MessageSet slice(long offset, long limit) throws IOException;


//    public void write(GetCommand getCommand, SessionContext ctx);
    public void write(GetCommand getCommand);

    public long append(ByteBuffer buff) throws IOException;


    public void flush() throws IOException;


    public void read(final ByteBuffer bf, long offset) throws IOException;


    public void read(final ByteBuffer bf) throws IOException;


    public long getMessageCount();

}