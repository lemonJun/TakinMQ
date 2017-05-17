package com.bj58.esb.server.store.buffer;

import com.taobao.gecko.core.buffer.IoBuffer;

/**
 * A {@link RuntimeException} which is thrown when the data the {@link IoBuffer}
 * contains is corrupt.
 * 
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 671827 $, $Date: 2008-06-26 10:49:48 +0200 (Thu, 26 Jun 2008)
 *          $
 * 
 */
public class BufferDataException extends RuntimeException {
    private static final long serialVersionUID = -4138189188602563502L;


    public BufferDataException() {
        super();
    }


    public BufferDataException(String message) {
        super(message);
    }


    public BufferDataException(String message, Throwable cause) {
        super(message, cause);
    }


    public BufferDataException(Throwable cause) {
        super(cause);
    }

}
