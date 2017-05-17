package com.bj58.spat.esb.server.daemon;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.bj58.spat.esb.server.communication.ESBContext;
import com.bj58.spat.esb.server.config.Client;
import com.bj58.spat.esb.server.config.SubjectFactory;
import com.bj58.spat.esb.server.exception.SerializeException;
import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.protocol.ESBSubject;
import com.bj58.spat.esb.server.util.ClientChannelListHelper;
import com.bj58.spat.esb.server.util.CountHelper;
import com.bj58.spat.esb.server.util.MessageCounter;
import com.bj58.spat.esb.server.util.SubjectChannelCount;

/**
 * Message handle 
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class MessageHandle {
	private static final Log logger = LogFactory.getLog(MessageHandle.class);
	private static ErrorWorker errorWorker = ErrorWorker.getInstrance();	
	private static final AtomicInteger  Count = new AtomicInteger(0);
	
	/**
	 * push message
	 * @param msg
	 * @throws CloneNotSupportedException
	 * @throws SerializeException 
	 */
	public static void publish(ESBMessage msg) throws CloneNotSupportedException, SerializeException {		
		List<Client> clientList = SubjectFactory.getSubjectFactory().getClientList(msg.getSubject());
		if (clientList != null) {
			for (Client client : clientList) {
				ESBMessage newMsg = msg.clone();
				newMsg.setClientID(client.getClientID());
				if (!publish(client, newMsg)) {
					if(!SubjectFactory.getSubjectFactory().getSubjectMap().get(newMsg.getSubject()).isCanAbandon()){
						errorWorker.offer(newMsg);
						logger.info("publish send fail message is " + newMsg.getMessageID()+newMsg.getClientID()+newMsg.getSubject());
					} 
				}
			}
		} else {
			/**
			 * 后期可以考虑推送给发送者消息
			 * */
			logger.error("[publish(ESBMessage msg) method] this subject: " + msg.getSubject() + " is not have!");
		}
	}
	
	
	/**
	 * 订阅
	 * 
	 * @param subList
	 * @throws Exception
	 */
	public static boolean subscribe(List<ESBSubject> subList, ESBContext ctx) throws Exception {
		return ClientChannelListHelper.subscribe(subList, ctx);
	}
	
	/***
	 * 推送消息
	 * @param client
	 * @param msg
	 * @return
	 */
	private static boolean publish(final Client client, final ESBMessage msg) {
		try {
			int count = client.getChannelList().size();
			if (count > 0) {
				Channel nettyChannel = null;
				while (count > 0) {
					//int idx = (int) (System.currentTimeMillis() & (count - 1));
					//int idx = Math.abs( Count.getAndIncrement()% count) ;
					SubjectChannelCount sChannelCount = CountHelper.getCountHelper().get(""+client.getSubject()+client.getClientID());
					int idx = 0;
					if(sChannelCount != null){
						idx = sChannelCount.getCount(count);
					}
					nettyChannel = client.getChannelList().get(idx);
					
					if (nettyChannel.isOpen()) {
						break;
					}else{
						ClientChannelListHelper.removeChannelList(client, nettyChannel);
					}
					--count;
				}
				
				if (nettyChannel == null) {
					return false;
				}
				
				if (!nettyChannel.isOpen()) {
					ClientChannelListHelper.removeChannelList(client, nettyChannel);
					return false;
				}
				
				MessageCounter.increase(new Integer(msg.getSubject()), MessageCounter.CounterType.OUT);//计数
				
				ChannelFuture future = nettyChannel.write(ChannelBuffers.copiedBuffer(msg.toBytes(), ESBMessage.DELIMITER));
				future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							if(!SubjectFactory.getSubjectFactory().getSubjectMap().get(msg.getSubject()).isCanAbandon()){
								ESBMessage newMsg = msg.clone();
								newMsg.setClientID(client.getClientID());
								errorWorker.offer(newMsg);
								logger.info("this message is send but not success::" + msg.getMessageID());
							}
							MessageCounter.decrease(new Integer(msg.getSubject()), MessageCounter.CounterType.OUT);//减去计数
						}
					}
				});
				return true;
			}
		} catch (Exception ex) {
			logger.error("send msg to client(id:" + client.getClientID() + ") error", ex);
		}
		return false;
	}
	
	
	/***
	 * 重新发送消息(DB拉取推送失败数据进行重新推送)
	 * @param client
	 * @param msg
	 * @return
	 */
	public static boolean publishRe(final Client client, final ESBMessage msg) {
		try {
			int count = client.getChannelList().size();
			if (count > 0) {
				Channel nettyChannel = null;
				while (count > 0) {
					//int idx = (int) (System.currentTimeMillis() & (count - 1));
					int idx = Math.abs( Count.getAndIncrement()% count) ;
					nettyChannel = client.getChannelList().get(idx);

					if (nettyChannel.isOpen()) {
						break;
					}else{
						ClientChannelListHelper.removeChannelList(client, nettyChannel);
					}
					--count;
				}

				if (nettyChannel == null) {
					return false;
				}
				if (!nettyChannel.isOpen()) {
					ClientChannelListHelper.removeChannelList(client, nettyChannel);
					return false;
				}
				
				MessageCounter.increase(new Integer(msg.getSubject()), MessageCounter.CounterType.OUT);//计数
				
				ChannelFuture future = nettyChannel.write(ChannelBuffers.copiedBuffer(msg.toBytes(), ESBMessage.DELIMITER));
				future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							logger.debug("this message is send but not success::" + msg.getMessageID());
							MessageCounter.decrease(new Integer(msg.getSubject()), MessageCounter.CounterType.OUT);//减去计数
						}
					}
				});
				return true;
			}
		} catch (Exception ex) {
			logger.error("send msg to client(id:" + client.getClientID() + ") error", ex);
		}
		return false;
	}
	
	
	
	/***
	 * Queue推送
	 * @param client
	 * @param msg
	 * @return
	 */
	public static boolean publishQueue(final Client client, final ESBMessage msg) {
		try {
			int count = client.getChannelList().size();
			if (count > 0) {
				Channel nettyChannel = null;
				while (count > 0) {
					//int idx = (int) (System.currentTimeMillis() & (count - 1));
					int idx = Math.abs( Count.getAndIncrement()% count);
					nettyChannel = client.getChannelList().get(idx);

					if (nettyChannel.isOpen()) {
						break;
					}else{
						ClientChannelListHelper.removeChannelList(client, nettyChannel);
					}
					--count;
				}

				if (nettyChannel == null) {
					return false;
				}
				if (!nettyChannel.isOpen()) {
					ClientChannelListHelper.removeChannelList(client, nettyChannel);
					return false;
				}
				
				MessageCounter.increase(new Integer(msg.getSubject()), MessageCounter.CounterType.OUT);//计数
				
				ChannelFuture future = nettyChannel.write(ChannelBuffers.copiedBuffer(msg.toBytes(), ESBMessage.DELIMITER));
				future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							logger.debug("this message is send but not success::" + msg.getMessageID());
							MessageCounter.decrease(new Integer(msg.getSubject()), MessageCounter.CounterType.OUT);//减去计数
						}
					}
				});
				return true;
			}
		} catch (Exception ex) {
			logger.error("send msg to client(id:" + client.getClientID() + ") error", ex);
		}
		return false;
	}
}
