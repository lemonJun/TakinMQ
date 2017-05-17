package com.bj58.spat.esb.server.config;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.channel.Channel;

public class Client {

	private int clientID;
	private int subject ;
	private List<Channel> channelList = new ArrayList<Channel>();

	public Client(int clientID,int subject) {
		this.clientID = clientID;
		this.subject = subject ;
	}
	
	public Client(int clientID,int subject, List<Channel> list){
		this.clientID = clientID;
		this.subject = subject ;
		this.channelList = list;
	}
	
	public int getSubject() {
		return subject;
	}

	public void setSubject(int subject) {
		this.subject = subject;
	}
	
	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	public void setChannelList(List<Channel> channelList) {
		this.channelList = channelList;
	}

	public List<Channel> getChannelList() {
		return channelList;
	}
	
}
