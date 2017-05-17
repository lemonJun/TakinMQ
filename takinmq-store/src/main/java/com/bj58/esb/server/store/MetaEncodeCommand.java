package com.bj58.esb.server.store;

import com.bj58.esb.server.store.buffer.IoBuffer;

/**
 * 协议编码接口和常量
 * 
 * 
 */
public interface MetaEncodeCommand {
    /**
     * 编码协议
     * 
     * @return 编码后的buffer
     */
    public IoBuffer encode();

    byte SPACE = (byte) ' ';
    byte[] CRLF = { '\r', '\n' };
    public String GET_CMD = "get";
    public String RESULT_CMD = "result";
    public String OFFSET_CMD = "offset";
    public String PUT_CMD = "put";
    public String SYNC_CMD = "sync";
    public String QUIT_CMD = "quit";
    public String VERSION_CMD = "version";
    public String STATS_CMD = "stats";
    public String TRANS_CMD = "transaction";
}