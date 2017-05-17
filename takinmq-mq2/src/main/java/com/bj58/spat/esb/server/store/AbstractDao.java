package com.bj58.spat.esb.server.store;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import com.bj58.spat.esb.server.config.ServiceConfig;
import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.store.filestore.FSQueue;

public class AbstractDao {
	public static final String DB_BASE_PATH = ServiceConfig.getInstrance().getString("dbpath"); //最后没有斜杠
	
	public static Map<String, FSQueue> path_db_map = new ConcurrentHashMap<String, FSQueue>();
	
	public static ReentrantLock lock = new ReentrantLock();
	
	protected FSQueue getDb(String dbpath) throws Exception{
		FSQueue db =  null ;
		lock.lock();
		db = path_db_map.get(dbpath);
		if(db== null){
			db= new FSQueue(dbpath);
			path_db_map.put(dbpath, db);
		}
		lock.unlock();
		return db ;
	}
	
	public void insertMessage(List<ESBMessage> messageList) throws Exception {
		for(int i = 0;i<messageList.size();i++){
			ESBMessage message = messageList.get(i);
			if(message!=null){
				FSQueue db =  getDb( DB_BASE_PATH+"/"+message.getSubject()+"/"+message.getClientID());
				db.add(message.toBytes());
			}
		}
	}
	
	public static void flushAllAndClose(){
		for(FSQueue db : path_db_map.values()){
			db.close();
		}
	}
}
