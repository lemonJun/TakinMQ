package com.bj58.spat.esb.server.communication.telnet;

import java.nio.ByteBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;

import com.bj58.spat.esb.server.communication.telnet.command.Command;

public class MonitorCenter {

	/**
	 * log
	 */
	private static Log logger = LogFactory.getLog(MonitorCenter.class);
	
	private static Command command = null;

	/**
	 * monitor event receive
	 * 
	 * @param e
	 * @throws Exception
	 * 
	 */
	public static void messageReceived(MessageEvent e) throws Exception {
		ByteBuffer buffer = ((ChannelBuffer) e.getMessage()).toByteBuffer();
		byte[] reciveByte = buffer.array();
		String msg = new String(reciveByte, "utf-8");
		
		command = Command.create(msg);
		logger.info("command:" + msg + "--commandType:" + command.getCommandType());
		command.exec(e);
		
		removeChannel(e.getChannel());
	}


	
	public static void removeChannel(Channel channel) {
		if(command != null) {
			command.removeChannel(channel);
		}
	}
}