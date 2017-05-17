package com.bj58.spat.esb.server.store.telnet;

public class MessageCounterEntity {
	private String id;//主键，subjectName+“_”+ ip
	private int subject;//subject
	private long ip;//server ip
	private int in;
	private int out ;
	private int persist;
	
	@Override
	public String toString() {
		return subject+"_"+ip;
	}
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getSubject() {
		return subject;
	}
	public void setSubject(int subject) {
		this.subject = subject;
	}
	public long getIp() {
		return ip;
	}
	public void setIp(long ip) {
		this.ip = ip;
	}
	public int getIn() {
		return in;
	}
	public void setIn(int in) {
		this.in = in;
	}
	public int getOut() {
		return out;
	}
	public void setOut(int out) {
		this.out = out;
	}
	public int getPersist() {
		return persist;
	}
	public void setPersist(int persist) {
		this.persist = persist;
	}
	
}
