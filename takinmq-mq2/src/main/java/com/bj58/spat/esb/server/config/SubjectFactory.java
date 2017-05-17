package com.bj58.spat.esb.server.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.channel.Channel;

public class SubjectFactory {
	
	private static final Lock lock = new ReentrantLock();
	private static SubjectFactory install = new SubjectFactory();
	private static final ReentrantLock channelLock = new ReentrantLock();
	private static ConcurrentHashMap<Integer, Subject> subject_client_Map= new ConcurrentHashMap<Integer, Subject>();
	
	private SubjectFactory(){
		
	}
	
	public static SubjectFactory getSubjectFactory(){
		return install;
	}
	
	public List<Client> getClientList(int subject){
		if(subject_client_Map.get(subject) != null){
			return subject_client_Map.get(subject).getClientList();
		}
		return null;
	}
	
	public ConcurrentHashMap<Integer, Subject> getSubjectMap(){
	return subject_client_Map;
}
	
	
	public List<Channel> getClientChannelList(String sub,int clientID){
		channelLock.lock();
		try{
			Subject subject = subject_client_Map.get(Integer.parseInt(sub));
			if(subject!=null)
			for(Client client : subject.getClientList()){
				if(client.getClientID() == clientID){
					return client.getChannelList();
				}
			}
			return new ArrayList<Channel>();
		}finally{
			channelLock.unlock();
		}
	}
	
	public void load(String url) throws Exception{
		lock.lock();
		try{
			subject_client_Map = SubjectConfig.getAllSubjectMap(url);
		}finally{
			lock.unlock();
		}
	}
}
