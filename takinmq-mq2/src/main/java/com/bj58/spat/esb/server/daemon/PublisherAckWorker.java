package com.bj58.spat.esb.server.daemon;

import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import com.bj58.spat.esb.server.communication.ESBContext;
import com.bj58.spat.esb.server.config.Subject;
import com.bj58.spat.esb.server.config.SubjectFactory;
import com.bj58.spat.esb.server.jsr166.LinkedTransferQueue;
import com.bj58.spat.esb.server.jsr166.TransferQueue;
import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.util.Common;
import com.bj58.spat.esb.server.util.MessageCounter;
import com.bj58.spat.esb.server.util.SystemUtils;

/**
 * 返回发送者ACK处理Worker
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class PublisherAckWorker {
	
	private static final Log logger = LogFactory.getLog(PublisherAckWorker.class);
	private static final int COUNT = SystemUtils.getHalfCpuProcessorCount();
	private static Thread[] workers = new Thread[COUNT];
	private static final TransferQueue<ESBContext> publisherAckQueue = new LinkedTransferQueue<ESBContext>();
	
	public PublisherAckWorker(){
		for(int i = 0; i < COUNT; i++){
			workers[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					for(;;){
						try{
							ESBContext ctx = publisherAckQueue.poll(1000, TimeUnit.MILLISECONDS);
							if(ctx != null){
								doAck(ctx);
							}
							Thread.sleep(1);
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
			});
			workers[i].setName("PublisherAckWorker");
			workers[i].setDaemon(true);
		}
	}
	
	public void offer(ESBContext ex){
		if(publisherAckQueue.size() > 50000){
			logger.info("PublisherAckWorker size > 50000");
		}
		publisherAckQueue.offer(ex);
	}
	
	public static int getQSize(){
		return publisherAckQueue.size();
	}
	
	
	public void start(){
		for(int i = 0; i < COUNT; i++){
			workers[i].start();
		}
	}
	
	/**
	 * 返回发送者ACK
	 * @param ctx
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public boolean doAck(ESBContext ctx) throws CloneNotSupportedException {	
		ESBMessage esbMessage = ESBMessage.fromBytes(ctx.getBuf());
		/**计数开始*/
		if(esbMessage.getMessageType() == 1){
			MessageCounter.increase(new Integer(esbMessage.getSubject()), MessageCounter.CounterType.PERSIST);
		}
		/**计数结束*/
		
		/**点对点*/
		Subject subject = SubjectFactory.getSubjectFactory().getSubjectMap().get(esbMessage.getSubject());
		if(subject==null||subject.isQueue()){
			return true;
		}
		
		esbMessage.setCommandType((byte) 1);
		
		try {
			ChannelFuture future = ctx.getChannel().write(ChannelBuffers.copiedBuffer(esbMessage.toBytes(), ESBMessage.DELIMITER));
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					if (!future.isSuccess()) {
						logger.info("this message ACK is send but not success");
					}
				}
			});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
