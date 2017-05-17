package com.bj58.spat.esb.server.communication.telnet.command;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;


public class Quit implements ICommandHelper{

	@Override
	public Command createCommand(String commandStr) {
		if(commandStr != null && !commandStr.equalsIgnoreCase("")) {
			if(commandStr.equalsIgnoreCase("quit")) {
				Command entity = new Command();
				entity.setCommandType(CommandType.Quit);
				return entity;
			}
		}
		return null;
	}
	
	
	@Override
	public void execCommand(Command command, MessageEvent event) throws Exception {
		if(command !=null && command.getCommandType() == CommandType.Quit) {
			event.getChannel().close();
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