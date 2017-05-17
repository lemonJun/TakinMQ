package com.bj58.esb.server.store.exception;

public class WrongPartitionException extends IllegalArgumentException {
	static final long serialVersionUID = 1123543565675L;
	
    public WrongPartitionException() {
        super();

    }


    public WrongPartitionException(String message, Throwable cause) {
        super(message, cause);

    }


    public WrongPartitionException(String s) {
        super(s);

    }


    public WrongPartitionException(Throwable cause) {
        super(cause);

    }

}