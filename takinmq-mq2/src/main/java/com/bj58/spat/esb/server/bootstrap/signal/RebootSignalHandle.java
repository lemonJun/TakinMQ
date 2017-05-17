package com.bj58.spat.esb.server.bootstrap.signal;

import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import sun.misc.Signal;
import sun.misc.SignalHandler;
import com.bj58.spat.esb.server.bootstrap.constant.ServerState;
import com.bj58.spat.esb.server.communication.TcpServer;
import com.bj58.spat.esb.server.daemon.ErrorWorker;
import com.bj58.spat.esb.server.daemon.PublisherAckWorker;
import com.bj58.spat.esb.server.daemon.QueueInsertWorker;
import com.bj58.spat.esb.server.daemon.SendWorker;
import com.bj58.spat.esb.server.exception.SerializeException;
import com.bj58.spat.esb.server.protocol.ESBMessage;

public class RebootSignalHandle implements SignalHandler{
	
	private static final Log logger = LogFactory.getLog(RebootSignalHandle.class);
	
	public static ESBMessage comand = new ESBMessage();
	
	static{
		comand.setCommandType((byte) 4);
	}

	@Override
	public void handle(Signal arg0) {
		ServerState.setRebooting(true);
		logger.info("server state is set to be rebooting ......");
		
		Iterator<Channel> iter =  TcpServer.allChannels.iterator();
		if(iter != null){
			while(iter.hasNext()){
				Channel nettyChannel = iter.next();
				if(nettyChannel!=null&&nettyChannel.isOpen()){
					try {
						nettyChannel.write(ChannelBuffers.copiedBuffer(comand.toBytes(), ESBMessage.DELIMITER));
					} catch (SerializeException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		try {
			Thread.sleep(1000 * 10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//各种队列等待清零开始
		while(true){
			try{
				Thread.sleep(100);
				if(	SendWorker.getQSize()  != 0 ){
					logger.info("SendWorker QSize is not 0!!");
					continue ;
				}
				if(	PublisherAckWorker.getQSize()  != 0 ){
					logger.info("PublisherAckWorker QSize is not 0!!");
					continue ;
				}
				
				if(	ErrorWorker.getErrorQSize()  != 0 ){
					logger.info("ErrorWorker ErrorQSize is not 0!!");
					continue ;
				}
				
				if(	QueueInsertWorker.getQSize()  != 0 ){
					logger.info("QueueWorker  QSize is not 0!!");
					continue ;
				}
				
				
			}catch(InterruptedException e){
				logger.info("exception when clear all Queue");
				continue ;
			}
			
			break ;	
		}
		//各种队列清零结束
		
		
		try {
			SendWorker.shutdown();
			Thread.sleep(1000 * 5);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.exit(0);//退出
	}

}
