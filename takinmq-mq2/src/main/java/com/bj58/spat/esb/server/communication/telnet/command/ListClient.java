package com.bj58.spat.esb.server.communication.telnet.command;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;

import com.bj58.spat.esb.server.config.Client;
import com.bj58.spat.esb.server.config.Subject;
import com.bj58.spat.esb.server.config.SubjectFactory;

/**
 * usage : 
 * 		list
 * 		list [-sub 101[,102]| -ip ip1[,ip2,ip3] | -c 1[,2,3]  ]
 * @author Administrator
 *
 */
public class ListClient implements ICommandHelper {

    @Override
    public Command createCommand(String commandStr) {
        if (commandStr != null && !commandStr.equalsIgnoreCase("")) {
            commandStr = commandStr.trim().toLowerCase();
            if (commandStr.startsWith("list")) {
                Command entity = new Command();
                entity.setCommandType(CommandType.List);

                commandStr = commandStr.replace("list", "").trim();

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
    public void execCommand(Command command, MessageEvent event) throws Exception {
        if (command != null && command.getCommandType() == CommandType.List) {

            StringBuffer sb = new StringBuffer("");

            sb.append(String.format("%18s%18s%18s%18s\r\n", "subjectName", "clientId", "ip", "port"));

            boolean useSub = command.getParamMap().get("-sub") != null;
            boolean useC = command.getParamMap().get("-c") != null;
            boolean useIp = command.getParamMap().get("-ip") != null;

            //Map<Integer, List<Client>> subjectMap= SubjectFactory.getSubjectFactory().getSubjectMap();
            Map<Integer, Subject> subjectMap = SubjectFactory.getSubjectFactory().getSubjectMap();

            //for(Entry<Integer, List<Client>> entry :subjectMap.entrySet()){
            for (Entry<Integer, Subject> entry : subjectMap.entrySet()) {
                int subjectName = entry.getKey();
                if (useSub && !command.getParamMap().get("-sub").contains(String.valueOf(subjectName))) {
                    continue;
                }
                List<Client> clientList = entry.getValue().getClientList();
                for (Client client : clientList) {
                    int clientId = client.getClientID();
                    if (useC && !command.getParamMap().get("-c").contains(String.valueOf(clientId))) {
                        continue;
                    }
                    List<Channel> channelList = client.getChannelList();
                    for (Channel channel : channelList) {

                        if (!channel.isOpen()) {
                            //channelList.remove(channel);
                            continue;
                        }
                        InetAddress addr = ((InetSocketAddress) channel.getRemoteAddress()).getAddress();

                        String address = addr.getHostAddress();
                        int port = ((InetSocketAddress) channel.getRemoteAddress()).getPort();
                        if (useIp && !command.getParamMap().get("-ip").contains(address)) {
                            continue;
                        }
                        sb.append(String.format("%18s%18s%18s%18s\r\n", String.valueOf(subjectName), String.valueOf(clientId), address, port));
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
