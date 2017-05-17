package com.bj58.spat.esb.server.daemon;

import java.util.Arrays;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.jboss.netty.channel.Channel;
import org.apache.commons.logging.LogFactory;
import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.jsr166.TransferQueue;
import com.bj58.spat.esb.server.jsr166.LinkedTransferQueue;

/**
 * asyn log worker
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class LogWorker  {
	
	private static final Log publishLog = LogFactory.getLog("publishLog");
	private static final Log subdesubLog = LogFactory.getLog("subAndUnsubLog");
	private static final TransferQueue<Object[]> logQueue = new LinkedTransferQueue<Object[]>();
	private static final TransferQueue<String> publishLogQueue = new LinkedTransferQueue<String>();
	
	private static Thread publishTread = null ;
	private static Thread logThread = null ;
	
	public static LogWorker logworker = new LogWorker();
	
	public void offerPublishLog(String log){
		publishLogQueue.offer(log);
	}
	
	
	public void offer(Object[] objs){
		logQueue.offer(objs);
	}
	
	private  LogWorker (){
		logThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						Object [] log = logQueue.poll(1000, TimeUnit.MILLISECONDS);
						if(log != null){
							byte[] reciveByte = (byte[]) log[0];
							InetAddress addr = ((InetSocketAddress)((Channel)log[1]).getRemoteAddress()).getAddress();
							
							if (reciveByte[6] == ESBMessage.PROTOCOL_TYPE) {
								if(reciveByte[5] == 0 || reciveByte[5] == 3){
									logworker.offerPublishLog("\t"+addr.getHostAddress()+"\t"+Arrays.toString(reciveByte));
								} 
							}else{
								subdesubLog.info("\t"+addr.getHostAddress()+"\t"+Arrays.toString(reciveByte));
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		publishTread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						String log = publishLogQueue.poll(1000, TimeUnit.MILLISECONDS);
						if(log != null){
							publishLog.info(log);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
	}
	
	public static void  start(){
		logThread.start();
		publishTread.start();
	}
}
