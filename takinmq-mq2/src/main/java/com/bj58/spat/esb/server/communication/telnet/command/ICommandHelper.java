package com.bj58.spat.esb.server.communication.telnet.command;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;


public interface ICommandHelper {
	public Command createCommand(String commandStr);
	public void execCommand(Command command, MessageEvent event) throws Exception;
	public void removeChannel(Command command, Channel channel);
	public int getChannelCount();
}