package com.bj58.spat.esb.server.store;

import java.util.Date;

import com.bj58.spat.esb.server.protocol.ESBMessage;

public class ESBReiciveMessage {

	private long id ;
	private Date addTimes;	
	private ESBMessage esbMessage;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getAddTimes() {
		return addTimes;
	}
	public void setAddTimes(Date addTimes) {
		this.addTimes = addTimes;
	}
	public ESBMessage getEsbMessage() {
		return esbMessage;
	}
	public void setEsbMessage(ESBMessage esbMessage) {
		this.esbMessage = esbMessage;
	}
	
	
}
