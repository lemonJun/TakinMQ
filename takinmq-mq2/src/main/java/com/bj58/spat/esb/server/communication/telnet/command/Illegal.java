package com.bj58.spat.esb.server.communication.telnet.command;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;

public class Illegal implements ICommandHelper {

    @Override
    public Command createCommand(String commandStr) {
        Command entity = new Command();
        entity.setCommandType(CommandType.Illegal);
        return entity;
    }

    @Override
    public void execCommand(Command command, MessageEvent event) throws Exception {
        if (command.getCommandType() == CommandType.Illegal) {
            String msg = "error: bad command please input again\r\n";
            byte[] responseByte = msg.getBytes("utf-8");
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