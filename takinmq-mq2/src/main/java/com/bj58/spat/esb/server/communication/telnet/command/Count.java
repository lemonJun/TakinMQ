package com.bj58.spat.esb.server.communication.telnet.command;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;

import com.bj58.spat.esb.server.config.Subject;
import com.bj58.spat.esb.server.config.SubjectFactory;
import com.bj58.spat.esb.server.util.MessageCounter;

public class Count implements ICommandHelper {

    @Override
    public Command createCommand(String commandStr) {
        if (commandStr != null && !commandStr.equalsIgnoreCase("")) {
            commandStr = commandStr.trim();
            if (commandStr.startsWith("count")) {
                Command entity = new Command();
                entity.setCommandType(CommandType.Count);
                commandStr = commandStr.replace("count", "").trim();
                Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
                if (!commandStr.equals("")) {
                    String params[] = commandStr.split("\\s+");
                    if (params.length % 2 != 0) {
                        return null;
                    }

                    for (int i = 0; i < params.length; i += 2) {
                        if (!params[i].startsWith("-")) {
                            return null;
                        }
                        String paramvs[] = params[i + 1].trim().split("\\,");
                        List<String> pvs = new ArrayList<String>();
                        for (String pv : paramvs) {
                            if (pv.matches("[0-9]+"))
                                pvs.add(pv);
                        }
                        paramMap.put(params[i], pvs);
                    }
                }
                entity.setParamMap(paramMap);
                return entity;
            }
        }
        return null;
    }

    @Override
    public void execCommand(Command command, MessageEvent event) throws UnsupportedEncodingException, NumberFormatException, InterruptedException {
        if (command != null && command.getCommandType() == CommandType.Count) {
            StringBuffer sb = new StringBuffer("");
            Map<String, List<String>> paramMap = command.getParamMap();

            if (paramMap.containsKey("-clean")) {
                List<String> subs = paramMap.get("-clean");
                for (String sub : subs) {
                    if (sub.matches("^\\d+$")) {
                        MessageCounter.clean(Integer.parseInt(sub));
                    }
                }
                sb.append("clean success !!!");
            } else {
                sb.append(String.format("%16s%16s%16s%16s\r\n", "subjectName", "in", "out", "persist"));
                //Map<Integer, List<Client>> subjectMap = SubjectFactory.getSubjectFactory().getSubjectMap();
                Map<Integer, Subject> subjectMap = SubjectFactory.getSubjectFactory().getSubjectMap();
                if (paramMap.size() == 0) {
                    for (Integer subject : subjectMap.keySet()) {
                        int[] count = MessageCounter.getCount(subject);
                        sb.append(String.format("%16s%16s%16s%16s\r\n", subject, count[0], count[1], count[2]));
                    }
                } else if (paramMap.get("-sub") != null) {
                    List<String> querySub = paramMap.get("-sub");
                    for (String sub : querySub) {
                        if (subjectMap.keySet().contains(Integer.parseInt(sub))) {
                            int[] count = MessageCounter.getCount(Integer.parseInt(sub));
                            sb.append(String.format("%16s%16s%16s%16s\r\n", sub, count[0], count[1], count[2]));
                        }
                    }
                }
            }

            byte[] responseByte = sb.toString().getBytes("utf-8");
            event.getChannel().write(ChannelBuffers.copiedBuffer(responseByte));

        }

    }

    @Override
    public void removeChannel(Command command, Channel channel) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getChannelCount() {
        // TODO Auto-generated method stub
        return 0;
    }

}
