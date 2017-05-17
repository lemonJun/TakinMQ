package com.bj58.spat.esb.server.daemon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.spat.esb.server.config.Client;
import com.bj58.spat.esb.server.config.SubjectFactory;
import com.bj58.spat.esb.server.jsr166.LinkedTransferQueue;
import com.bj58.spat.esb.server.jsr166.TransferQueue;
import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.store.QueueMessageDao;

/**
 * 同步队列worker
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class QueueInsertWorker {
	private static final Log logger = LogFactory.getLog(QueueInsertWorker.class);
	private static final QueueMessageDao queueMessageDao = new QueueMessageDao();
	private static final TransferQueue<ESBMessage> iQueue = new LinkedTransferQueue<ESBMessage>();
	private Thread[] iQueWorkers;
	private static final int COUNT = 1;
	
	public static int getQSize(){
		return iQueue.size();
	}
	
	
	public static void inQueueWorker(ESBMessage msg) throws CloneNotSupportedException{
		List<Client> clientList = SubjectFactory.getSubjectFactory().getClientList(msg.getSubject());
		
		if (clientList != null) {
			for (Client client : clientList) {
				ESBMessage newMsg = msg.clone();
				newMsg.setClientID(client.getClientID());
				/**根据主题对应的clientID分别入库*/
				iQueue.offer(newMsg);
			}
		}
	}
	
	public void start(){
		for(Thread t: iQueWorkers){
			t.start();
		}
	}
	public QueueInsertWorker(){
		iQueWorkers = new Thread[COUNT];
		for (int i = 0; i < COUNT; i++) {
			iQueWorkers[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					List<ESBMessage> msgList = new ArrayList<ESBMessage>(50);
					while (true) {
						try {
							ESBMessage message = iQueue.poll(1000, TimeUnit.MILLISECONDS);
							if(message != null) {
								msgList.add(message);
								if(msgList.size() == 50){
									try{
										queueMessageDao.insertMessage(msgList);
									}finally{
										msgList.clear();
									}
									logger.debug("insert into message and pool size is "+iQueue.size());
								}
							}else{
								if(msgList.size() > 0){
									try{
										queueMessageDao.insertMessage(msgList);
									}finally{
										msgList.clear();
									}
								}
							}
							message = null;
							Thread.sleep(1);
						} catch (Exception ex) {
							logger.error("QueueWorker insertThread write error", ex);
						}
					}
				}
			});
			iQueWorkers[i].setName("QueueWorker thread[" + i + "]");
			iQueWorkers[i].setDaemon(true);
		}
	}
}
