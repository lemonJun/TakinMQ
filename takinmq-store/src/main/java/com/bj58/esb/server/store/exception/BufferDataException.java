package com.bj58.esb.server.store.exception;

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
