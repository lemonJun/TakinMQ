package com.bj58.spat.esbclient.exception;

public class CommunicationException extends Exception{

	private static final long serialVersionUID = -3864991520786547002L;

	public CommunicationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CommunicationException(String message) {
		super(message);
	}

	public CommunicationException(Throwable cause) {
		super(cause);
	}
	
	public CommunicationException() {
		super("comunication exception");
	}
}
