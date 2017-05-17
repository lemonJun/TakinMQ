package com.bj58.spat.esb.server.daemon;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.spat.esb.server.bootstrap.constant.ServerState;
import com.bj58.spat.esb.server.config.Client;
import com.bj58.spat.esb.server.config.SubjectFactory;
import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.store.QueueMessageDao;

/**
 * Queue处理worker
 */
public class QueueSendWorker extends Thread{
	
	private int subject ;
	private int clientId ;
	private static final QueueMessageDao queueMessageDao = new QueueMessageDao();
	private static final Log logger = LogFactory.getLog(ErrorWorker.class);
	
	public QueueSendWorker(int subject ,int clientId ){
		this.subject = subject ;
		this.clientId = clientId;
	}
	
	@Override
	public void run() {
		for(;;){
			try {
				if(ServerState.isRunning()){
					this.send();
				}else{
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void send() throws Exception{
		ESBMessage message = null ;
		message = queueMessageDao.readMessage(subject,clientId);
		if(message != null){
			boolean bool = publishQue(message);
			if(!bool){
				queueMessageDao.unDeleteMessage(message);
				Thread.sleep(500);
			} 
		}else{
			Thread.sleep(500);
		}
	}
	
	
	/**
	 * 推送
	 * @param msg
	 * @return
	 * @throws CloneNotSupportedException
	 */
	private boolean publishQue(ESBMessage msg) throws CloneNotSupportedException {
		Client tClient = null;
		List<Client> clientList = SubjectFactory.getSubjectFactory().getClientList(msg.getSubject());
		if (clientList != null) {
			for (Client client : clientList) {
				if (msg.getClientID() == client.getClientID()) {
					tClient = client;
					break;
				}
			}
		} else {
			logger.info("this subject[" + msg.getSubject() + "] not hava clientID!");
		}
		
		if (tClient == null) {
			return true;
		}
		return MessageHandle.publishQueue(tClient, msg);
	}
	
}
