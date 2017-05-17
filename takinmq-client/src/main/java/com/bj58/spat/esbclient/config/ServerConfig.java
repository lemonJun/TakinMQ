package com.bj58.spat.esbclient.config;

public class ServerConfig {
	
	private String ip;
	private int port;
	private int initConn;
	private boolean keepAlive;
	private int connectTimeOut;
	private boolean nagle;
	private int maxPakageSize;
	private int recvBufferSize;
	private int sendBufferSize;
	
	public ServerConfig() {
		super();
		this.initConn = 1;
		this.keepAlive = true;
		this.connectTimeOut = 1000 * 3;
		this.nagle = false;
		this.maxPakageSize = 1024 * 1024 * 2;
		this.recvBufferSize = 1024 * 1024 * 2;
		this.sendBufferSize = 1024 * 1024 * 2;
	}
	

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getInitConn() {
		return initConn;
	}

	public void setInitConn(int initConn) {
		this.initConn = initConn;
	}


	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public int getConnectTimeOut() {
		return connectTimeOut;
	}

	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	public boolean isNagle() {
		return nagle;
	}

	public void setNagle(boolean nagle) {
		this.nagle = nagle;
	}

	public int getMaxPakageSize() {
		return maxPakageSize;
	}

	public void setMaxPakageSize(int maxPakageSize) {
		this.maxPakageSize = maxPakageSize;
	}

	public int getRecvBufferSize() {
		return recvBufferSize;
	}

	public void setRecvBufferSize(int recvBufferSize) {
		this.recvBufferSize = recvBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

}
