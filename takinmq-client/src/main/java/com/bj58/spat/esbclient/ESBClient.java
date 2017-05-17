package com.bj58.spat.esbclient;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.apache.commons.logging.Log;
import java.util.concurrent.Executors;
import org.apache.commons.logging.LogFactory;
import com.bj58.spat.esbclient.config.ESBConfig;
import com.bj58.spat.esbclient.communication.Server;
import com.bj58.spat.esbclient.communication.ServerPool;
import com.bj58.spat.esbclient.exception.SerializeException;
import com.bj58.spat.esbclient.exception.CommunicationException;
import com.bj58.spat.esbclient.exception.ConnectTimeoutException;

public class ESBClient {
	
	private static final Log logger = LogFactory.getLog(ESBClient.class);
	
	private ServerPool servPool;
	private ESBConfig esbConfig;

	/**
	 * @param url tcp://10.58.120.110:12345,10.58.120.110:12345,10.58.120.110:12345?clientID=123&xxx=abc
	 * @throws IOException 
	 */
	public ESBClient(String url) throws IOException  {
		this(url, Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * 获取ESBClient实例
	 * @param url
	 * @return
	 * 备注：兼容ActiveMQ版本方法
	 */
	@Deprecated
	public static ESBClient getInstance(String url){
		try {
			return new ESBClient(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param url
	 * @param workerCount
	 * @throws IOException
	 */
	public ESBClient(String url, int workerCount) throws IOException {
		logger.info("starting ESBClient workerCount:" + workerCount + " url:" + url);
		this.esbConfig = ESBConfig.getConfigFromURL(url);
		if(workerCount > 0) {
			this.esbConfig.setExecutor(Executors.newFixedThreadPool(workerCount));
		} else {
			this.esbConfig.setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
		}
		this.servPool = new ServerPool(esbConfig,this);
		logger.info("ESBClient start success");
	}
	
	/**
	 * 发送消息
	 * @param msg
	 */
	public void send(ESBMessage msg) {
		if(null != msg){
			msg.setClientID(esbConfig.getClientID());
			servPool.send(msg);
		}
	}
	
	/**
	 * 发送消息(顺序性)
	 * @param msg
	 */
	public void sendQueue(ESBMessage msg) {
		if(null != msg){
			msg.setClientID(esbConfig.getClientID());
			servPool.sendQueue(msg);
		}
	}
	
	/**
	 * 发送消息
	 * @param message
	 * @throws Exception
	 * 备注：兼容ActiveMQ版本方法
	 */
	@Deprecated
	public void postMessage(ESBMessage message) throws Exception{
		Server server = servPool.getServer(false);
		try {
			message.setClientID(esbConfig.getClientID());
			server.sendMessage(message);
		} catch (IOException e) {
			logger.error("send message error", e);
		} catch (ConnectTimeoutException e) {
			logger.error("send message error", e);
		} catch (SerializeException e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭连接
	 * @param subject
	 * 备注：兼容ActiveMQ版本方法
	 */
	@Deprecated
	public void cleanup(int subject){
		
	}
	
	/**
	 * 添加订阅主题
	 * @param subject
	 * @throws ConnectTimeoutException 
	 * @throws IOException 
	 * @throws SerializeException 
	 * @throws CommunicationException 
	 */
	public void setReceiveSubject(ESBSubject ... subjects) throws IOException, ConnectTimeoutException, SerializeException, CommunicationException {
		for(ESBSubject sbj : subjects) {
			sbj.setClientID(esbConfig.getClientID());
			logger.info("subscribe subjectID:" + sbj.getSubjectID() + " clientID:" + esbConfig.getClientID());
		}
		
		List<Server> servList = servPool.getAllServer();
		for(Server serv : servList) {
			serv.subscribe(subjects);
			logger.info("subscribe success on " + serv.toString());
		}
	}
	
	/**
	 * 设置消息接收handler
	 * @param handler
	 */
	public void setReceiveHandler(ESBReceiveHandler handler) {
		this.esbConfig.setReceiveHandler(handler);
	}
	
	/**
	 * 关闭所有连接并获取队列中未处理的消息
	 * @return
	 */
	@Deprecated
	public List<ESBMessage> closeAllChannelAndGetMessage(){
		closeAllChannel();
		//servPool.close();
//		TransferQueue<Runnable> nitifyQ = new LinkedTransferQueue<Runnable>();
//		for(Server server : servPool.getAllServer()){
//			for(NIOChannel cnl : server.getChannelPool().getAllChannel()){
//				nitifyQ.addAll(cnl.getReceiveWorker().getNotifyQueue());
//			}
//		}
//		Runnable revMsg = null ;
		List<ESBMessage> list = new ArrayList<ESBMessage>();
//		try {
//			while((revMsg = nitifyQ.poll(1000, TimeUnit.MILLISECONDS))!=null){
//				
//				if(revMsg instanceof NotifyTask){
//					
//				}
//				
//				//list.add(ESBMessage.fromBytes(revMsg.getData()));
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		return list ;
	}
	
	public void closeAllChannel(){
		servPool.close();
		servPool.getServerReciveHandler().shutdown();
	}
}
