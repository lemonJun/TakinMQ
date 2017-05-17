package com.bj58.spat.esb.server.communication.telnet.command;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;

public class Help implements ICommandHelper {

	@Override
	public Command createCommand(String commandStr) {
		if(commandStr != null && !commandStr.equalsIgnoreCase("")) {
			if(commandStr.equalsIgnoreCase("help")) {
				Command entity = new Command();
				entity.setCommandType(CommandType.Help);
				return entity;
			}
		}
		return null;
	}

	
	@Override
	public void execCommand(Command command, MessageEvent event) throws Exception {
		if(command !=null && command.getCommandType() == CommandType.Help) {
			StringBuilder sbMsg = new StringBuilder();
			sbMsg.append("*******************************************************************\r\n\n");
			
			sbMsg.append("count [-sub 101[,102|,103]]\r\n");
			sbMsg.append("list [-sub 101[,102]| -ip ip1[,ip2,ip3] | -c 1[,2,3]  ]\r\n");
			sbMsg.append("view [-subscribeQ | -sendQ | -resendQ | -publisherQ | -publisherackQ | -subscriberackQ | -errorQ | -delQ | -checkerrorQ | -checkQ | -checkM | -dbcache |- messageCount] [-t 5]\r\n");
			sbMsg.append("quit\r\n");
			sbMsg.append("help\r\n");
			
			sbMsg.append("*******************************************************************\r\n\n");
			byte[] responseByte = sbMsg.toString().getBytes("utf-8");
			event.getChannel().write(ChannelBuffers.copiedBuffer(responseByte));
		}
	}
	
	@Override
	public void removeChannel(Command command, Channel channel) {
		// do nothing
	}
	
	@Override
	public int getChannelCount() {
		return 0;
	}
}