package com.bj58.spat.esb.server.store;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.store.filestore.FSQueue;

public class EsbMessageDao extends AbstractDao  {
	
	private static final Log log = LogFactory.getLog(EsbMessageDao.class);
	public void insertMessage(ESBMessage message) throws Exception {
		FSQueue db =  getDb( DB_BASE_PATH+"/"+message.getSubject()+"/"+message.getClientID());
		db.add(message.toBytes());
	}
	
	public List<ESBMessage> readMessage(int number, int subject, int clientId)
			throws Exception {
		FSQueue db =  getDb( DB_BASE_PATH+"/"+subject+"/"+clientId);
		byte[][] result = db.readNextAndRemove(number);
		List<ESBMessage> list = new ArrayList<ESBMessage>();
		if(result == null ){
			log.info("readNextAndRemove:null;"+"number:"+number);
			return list;
		}
		for(byte[] oneResult : result){
			list.add(ESBMessage.fromBytes(oneResult));
		}
		return list;
	}
	
	public void goBack(int i,int subject,int clientId ) throws Exception{
		FSQueue db =  getDb( DB_BASE_PATH+"/"+subject+"/"+clientId);
		db.readGoBack(i);
		log.info("subject : "+ subject+"  ;clientid:"+ clientId+" goback; length:"+i);
	}
}
