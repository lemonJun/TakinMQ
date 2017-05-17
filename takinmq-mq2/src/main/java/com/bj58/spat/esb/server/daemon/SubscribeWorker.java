package com.bj58.spat.esb.server.daemon;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.bj58.spat.esb.server.communication.ESBContext;
import com.bj58.spat.esb.server.config.Subject;
import com.bj58.spat.esb.server.config.SubjectFactory;
import com.bj58.spat.esb.server.jsr166.LinkedTransferQueue;
import com.bj58.spat.esb.server.jsr166.TransferQueue;
import com.bj58.spat.esb.server.protocol.ESBSubject;
import com.bj58.spat.esb.server.util.Common;
import com.bj58.spat.esb.server.util.ThreadPoolService;

public class SubscribeWorker {

	private static final TransferQueue<ESBContext> subscribeQueue = new LinkedTransferQueue<ESBContext>();
	private Thread[] workers;
	private static final int COUNT = 1;
	
	public void offer(ESBContext context){
		subscribeQueue.offer(context);
	}
	
	public static int getQSize(){
		return subscribeQueue.size();
	}
	public SubscribeWorker(){
		workers = new Thread[COUNT];
		for (int i = 0; i < COUNT; i++) {
			workers[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					for(;;){
						try{
							ESBContext ctx = subscribeQueue.poll(1000, TimeUnit.MILLISECONDS);
							if(ctx != null){
								/** 订阅 */
								List<ESBSubject> subList = ESBSubject.fromBytes(ctx.getBuf());
								MessageHandle.subscribe(subList, ctx);
								
								/**拉库重发	 */
								for(ESBSubject subjectEntity : subList){
									/**是否订阅点对点消息*/
									Subject subject = SubjectFactory.getSubjectFactory().getSubjectMap().get(subjectEntity.getSubjectID());
									if(subject == null){
										continue ;
									}
									if(subject.isQueue()){
										QueueSendWorker qsw = Common.getQueSendWorker(""+subjectEntity.getSubjectID()+subjectEntity.getClientID());
										if(qsw == null){
											qsw = new QueueSendWorker(subjectEntity.getSubjectID(), subjectEntity.getClientID());
											qsw.start();
											Common.putQueSendWorker(""+subjectEntity.getSubjectID()+subjectEntity.getClientID(), qsw);
										}
									}else{
										ThreadPoolService.executor.execute(ErrorWorker.getInstrance().new SendWorker(subjectEntity.getSubjectID(), subjectEntity.getClientID()));
									}
								}
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
			});
			workers[i].setName("SubscribeWorker thread[" + i + "]");
			workers[i].setDaemon(true);
		}
	}
	
	
	public void start(){
		for (int i = 0; i < COUNT; i++) {
			workers[i].start();
		}	        
	}

}
