package com.bj58.spat.esbclient.exception;

public class ConnectTimeoutException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConnectTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectTimeoutException(String message) {
        super(message);
    }

    public ConnectTimeoutException(Throwable cause) {
        super(cause);
    }

    public ConnectTimeoutException() {
        super("connection timeout exception");
    }
}
