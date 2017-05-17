package com.bj58.esb.server.store.exception;

public class InvalidSystemClock extends RuntimeException {
	static final long serialVersionUID = 3543354565675L;
    public InvalidSystemClock() {
        super();

    }


    public InvalidSystemClock(String message, Throwable cause) {
        super(message, cause);

    }


    public InvalidSystemClock(String message) {
        super(message);

    }


    public InvalidSystemClock(Throwable cause) {
        super(cause);

    }

}