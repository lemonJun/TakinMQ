package com.bj58.spat.esbclient.communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.bj58.spat.esbclient.ESBSubject;

class DaemonChecker implements Runnable {
	
	private static final Log logger = LogFactory.getLog(DaemonChecker.class);
	private static final List<Server> checkList = new ArrayList<Server>();
	private static final Object locker = new Object();
	private Server serv ;
	
	public DaemonChecker(Server serv){
		this.serv = serv ;
	}
	
	public static void check(Server serv) {
		serv.setState(ServerState.Testing);
		
		synchronized (locker) {
			if(!checkList.contains(serv)) {
				checkList.add(serv);
				Thread thread = new Thread(new DaemonChecker(serv));
				thread.setName("ESBClient server state checker");
				thread.setDaemon(true);
				thread.start();
			}
		}
	}
	
	private static boolean ping(Server serv) {
		String serverIp = serv.getServerConfig().getIp();
		int port = serv.getServerConfig().getPort();
		
		String strAddr = serverIp
						+ ":"
						+ port; 
		logger.warn("test server:" + strAddr);
		
		Socket sock = null;
		
		try {
			InetSocketAddress addr = new InetSocketAddress(serverIp, port);
			sock = new Socket();
			sock.connect(addr, 1000 * 3);
			return sock.isConnected();
		} catch (IOException e) {
			logger.info("ESBServer(" + strAddr + ") is dead");
		}finally{
			if(sock != null){
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	

	@Override
	public void run() {
		while(true) {
			try {
				if(this.serv != null) {
					if(ping(serv)) {
						//探测服务器活过来了则订阅一次
						serv.connect();
						serv.setState(ServerState.Normal);
						/**订阅者重新订阅*/
						ESBSubject sujectArray[] = serv.getSubjectArray();
						if(sujectArray != null && sujectArray.length > 0 ){
							serv.subscribe(sujectArray);
						}
						synchronized (locker) {
							checkList.remove(serv);
						}
						break ;
					} else {
						serv.setState(ServerState.Dead);
					}
				}
				Thread.sleep(1000 * 10);
			} catch(Exception ex) {
				logger.error("checker thread error", ex);
			}
		}
	}
}
