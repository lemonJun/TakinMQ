package com.bj58.spat.esb.server.store.telnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MessageCounterDaoMongo implements IMessageCounterDao{
	
	private static MessageCounterDaoMongo instance = new MessageCounterDaoMongo();
	private static Map<String ,MessageCounterEntity> allEntity = new ConcurrentHashMap<String ,MessageCounterEntity>();
	
	private MessageCounterDaoMongo(){
		
	}
	
	public static IMessageCounterDao getInstance(){
		return instance ;
	}

	@Override
	public void insert(MessageCounterEntity entity) {
		allEntity.put(entity.getSubject()+"_"+entity.getIp(), entity);
	}

	@Override
	public List<MessageCounterEntity> load(long ip) {
		List<MessageCounterEntity> result = new ArrayList<MessageCounterEntity>();
		
		for(MessageCounterEntity entity : allEntity.values()){
			if(entity.getIp()==ip){
				result.add(entity);
			}
		}
		
		return result;
	}

	@Override
	public void update(MessageCounterEntity entity) {
		allEntity.put(entity.getSubject()+"_"+entity.getIp(), entity);
	}

	@Override
	public MessageCounterEntity get(String id) {
		return allEntity.get(id);
	}
}
