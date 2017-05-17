package com.bj58.spat.esb.server.util;

import java.util.concurrent.ConcurrentHashMap;

import com.bj58.spat.esb.server.daemon.QueueSendWorker;

public class Common {
	private static ConcurrentHashMap<String, QueueSendWorker> qsw = new ConcurrentHashMap<String, QueueSendWorker>();
	
	public static void putQueSendWorker(String str, QueueSendWorker qw){
		qsw.put(str, qw);
	}
	
	public static QueueSendWorker getQueSendWorker(String str){
		return qsw.get(str);
	}
	
	public static void removeQueSendWorker(String str){
		qsw.remove(str);
	}
	
}
