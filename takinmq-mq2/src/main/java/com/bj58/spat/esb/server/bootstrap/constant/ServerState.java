package com.bj58.spat.esb.server.bootstrap.constant;

public class ServerState {
	
	private static volatile boolean isRebooting = false ;

	public static boolean isRebooting() {
		return isRebooting;
	}

	public static void setRebooting(boolean isRebooting) {
		ServerState.isRebooting = isRebooting;
	}
	
	public static boolean isRunning() {
		return !isRebooting;
	}

}
