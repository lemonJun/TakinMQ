package com.bj58.spat.esb.server.store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.store.filestore.FSQueue;

/**
 * 顺序性Dao
 */
public class QueueMessageDao extends AbstractDao {
	
	private static final Log log = LogFactory.getLog(QueueMessageDao.class);
	
	public ESBMessage readMessage(int subject, int clientId) throws Exception {
		FSQueue db =  getDb( DB_BASE_PATH+"/"+subject+"/"+clientId);
		byte[] m =db.readNextAndRemove();
		if(m!=null && m.length!=0){
			return ESBMessage.fromBytes(m);
		}
		return null;
	}
	
	public void unDeleteMessage(ESBMessage messaeg) throws Exception{
		FSQueue db =  getDb( DB_BASE_PATH+"/"+messaeg.getSubject()+"/"+messaeg.getClientID());
		db.readGoBack(4+messaeg.toBytes().length);
		log.info("subject : "+ messaeg.getSubject()+"  ;clientid:"+ messaeg.getClientID()+" qgoback; length:"+(4+messaeg.toBytes().length));
	}
	
}
