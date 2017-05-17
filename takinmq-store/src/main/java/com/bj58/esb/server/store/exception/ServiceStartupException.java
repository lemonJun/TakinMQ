package com.bj58.esb.server.store.exception;

public class ServiceStartupException extends RuntimeException {
	static final long serialVersionUID = 1123543565675L;
	
    public ServiceStartupException() {
        super();

    }


    public ServiceStartupException(final String message, final Throwable cause) {
        super(message, cause);

    }


    public ServiceStartupException(final String message) {
        super(message);

    }


    public ServiceStartupException(final Throwable cause) {
        super(cause);

    }

}