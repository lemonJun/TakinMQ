package com.bj58.esb.server.store.exception;

public class InvalidOffsetStorageException extends IllegalArgumentException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public InvalidOffsetStorageException() {
        super();

    }


    public InvalidOffsetStorageException(String message, Throwable cause) {
        super(message, cause);

    }


    public InvalidOffsetStorageException(String s) {
        super(s);

    }


    public InvalidOffsetStorageException(Throwable cause) {
        super(cause);

    }

}