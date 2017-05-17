package com.bj58.spat.esb.server.store;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.bj58.spat.esb.server.config.ServerConfigBase;

public class MongoBase {
	
	private static long INFOID_FLAG = 1260000000000L;
	private static final int SERVER_ID = ServerConfigBase.getServerID();
	private static final Lock infoLock = new ReentrantLock();
	
	
	private static long count = 0;
	public static long GenInfoID() throws Exception {
		infoLock.lock();
		
		count++;
		
		if(count > 15){
			count = 0;
		}
		
		try{
			long infoid = System.nanoTime() - INFOID_FLAG;
			infoid = (infoid<<7) | SERVER_ID | (count<<3);
			return infoid;
		}finally{
			infoLock.unlock();
		}
	}
}
