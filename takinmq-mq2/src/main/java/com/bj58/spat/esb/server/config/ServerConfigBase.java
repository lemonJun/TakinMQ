package com.bj58.spat.esb.server.config;

import com.bj58.spat.esb.server.util.SystemUtils;

public class ServerConfigBase {
	
	/**超时时间*/
	public static final long OUT_TIME = 30000;
	
	public static String getServerName(){
		return ServiceConfig.getInstrance().getString("esb.service.name");
	}
	
	public static int getServerID(){
		int id = 1;
		try {
			 id = ServiceConfig.getInstrance().getInt("esb.service.id");
		} catch (Exception e) {
			try {
				throw new Exception("server id is error,please check config file!");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return id;
	}
	
	/**
	 * 服务监听IP
	 * @return
	 */
	public static String getServerListenIP(){
		return ServiceConfig.getInstrance().getString("esb.server.listenIP");
	}
	
	/**
	 * 服务监听port
	 * @return
	 * @throws Exception 
	 */
	public static int getServerListenPort() throws Exception{
		return ServiceConfig.getInstrance().getInt("esb.server.listenPort");
	}
	
	public static int getReceiveBufferSize() throws Exception{
		return ServiceConfig.getInstrance().getInt("esb.server.receiveBufferSize");
	}
	
	public static int getSendBufferSize() throws Exception{
		return ServiceConfig.getInstrance().getInt("esb.server.sendBufferSize");
	}
	
	public static int getFrameMaxLength() throws Exception{
		return ServiceConfig.getInstrance().getInt("esb.server.frameMaxLength");
	}
	
	public static int getWorkerCount() throws Exception{
		return ServiceConfig.getInstrance().getInt("esb.server.workerCount");
	}
	
	public static int getPushWorkerCount(){
		int worker_count = SystemUtils.getSystemThreadCount();
		try {
			worker_count =  ServiceConfig.getInstrance().getInt("esb.server.push.workerCount");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return worker_count;
	}
}
