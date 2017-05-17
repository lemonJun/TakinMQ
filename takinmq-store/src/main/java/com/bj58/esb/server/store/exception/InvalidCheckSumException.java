package com.bj58.esb.server.store.exception;

public class InvalidCheckSumException extends IllegalArgumentException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public InvalidCheckSumException() {
        super();

    }


    public InvalidCheckSumException(String message, Throwable cause) {
        super(message, cause);

    }


    public InvalidCheckSumException(String s) {
        super(s);

    }


    public InvalidCheckSumException(Throwable cause) {
        super(cause);

    }

}