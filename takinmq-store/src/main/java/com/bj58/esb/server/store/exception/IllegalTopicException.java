package com.bj58.esb.server.store.exception;

public class IllegalTopicException extends RuntimeException {
	static final long serialVersionUID = 3543565675L;
	
    public IllegalTopicException() {
        super();

    }


    public IllegalTopicException(String message, Throwable cause) {
        super(message, cause);

    }


    public IllegalTopicException(String message) {
        super(message);

    }


    public IllegalTopicException(Throwable cause) {
        super(cause);

    }

}