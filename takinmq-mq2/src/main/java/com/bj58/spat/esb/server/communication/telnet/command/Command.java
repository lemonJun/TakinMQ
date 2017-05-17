package com.bj58.spat.esb.server.communication.telnet.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;

/**
 * 
 */
public class Command {

    private CommandType commandType;

    private String command;

    private Map<String, List<String>> paramMap = new HashMap<String, List<String>>();

    /**
     * 
     */
    private static List<ICommandHelper> helperList = new ArrayList<ICommandHelper>();

    static {
        /*helperList.add(new CRLF());
        helperList.add(new Quit());
        helperList.add(new Count());
        helperList.add(new Exec());
        helperList.add(new Time());
        helperList.add(new Help());
        helperList.add(new Control());
        helperList.add(new Illegal());*/
        helperList.add(new Help());
        helperList.add(new Quit());
        helperList.add(new Count());
        helperList.add(new ListClient());
        helperList.add(new View());
        helperList.add(new Illegal());
    }

    /**
     * 
     * @param command
     * @return
     */
    public static Command create(String command) {
        Command entity = null;
        command = command.trim();
        for (ICommandHelper cc : helperList) {
            entity = cc.createCommand(command);
            if (entity != null) {
                break;
            }
        }
        if (entity == null) {
            entity = new Command();
            entity.setCommandType(CommandType.Illegal);
        }
        return entity;
    }

    /**
     * exec command
     * @return
     * @throws Exception
     */
    public void exec(MessageEvent event) throws Exception {
        for (ICommandHelper cc : helperList) {
            cc.execCommand(this, event);
        }
    }

    /**
     * 
     * @param channel
     */
    public void removeChannel(Channel channel) {
        for (ICommandHelper cc : helperList) {
            cc.removeChannel(this, channel);
        }
    }

    /**
     * get channel count
     * @return
     */
    public int getChannelCount() {
        int count = 0;
        for (ICommandHelper cc : helperList) {
            count += cc.getChannelCount();
        }
        return count;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Map<String, List<String>> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, List<String>> paramMap) {
        this.paramMap = paramMap;
    }

}