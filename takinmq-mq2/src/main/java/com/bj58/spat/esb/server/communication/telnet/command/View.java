package com.bj58.spat.esb.server.communication.telnet.command;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;

import com.bj58.spat.esb.server.daemon.ErrorWorker;
import com.bj58.spat.esb.server.daemon.PublisherAckWorker;
import com.bj58.spat.esb.server.daemon.SendWorker;
import com.bj58.spat.esb.server.daemon.SubscribeWorker;

public class View implements ICommandHelper {

    private static Map<String, Object> paramQueueMap = new HashMap<String, Object>();

    static {
        paramQueueMap.put("-subscribeQ", SubscribeWorker.getQSize());//处理订阅动作的队列
        paramQueueMap.put("-sendQ", SendWorker.getQSize());//服务器端往客户端推送的队列
        paramQueueMap.put("-publisherackQ", PublisherAckWorker.getQSize());//给发布者ack的队列
        paramQueueMap.put("-errorQ", ErrorWorker.getErrorQSize());//发送时失败的队列
    }

    @Override
    public Command createCommand(String commandStr) {
        if (commandStr != null && !commandStr.equalsIgnoreCase("")) {
            commandStr = commandStr.trim();
            if (commandStr.startsWith("view")) {
                Command entity = new Command();
                entity.setCommandType(CommandType.View);

                commandStr = commandStr.replace("view", "").trim();

                Map<String, List<String>> paramMap = new HashMap<String, List<String>>();

                if (!commandStr.equals("")) {
                    String params[] = commandStr.split("\\s+");
                    for (int i = 0; i < params.length;) {
                        if (params[i].trim().equals("")) {
                            i++;
                            continue;
                        }
                        if (!params[i].trim().equals("-t")) {
                            paramMap.put(params[i].trim(), null);
                            i++;
                        } else {
                            if (((i + 1) == params.length) || !params[i + 1].matches("[0-9]+")) {
                                return null;
                            }
                            List<String> list = new ArrayList<String>();
                            list.add(params[i + 1].trim());
                            paramMap.put("-t", list);
                            i += 2;
                        }
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
        if (command != null && command.getCommandType() == CommandType.View) {

            StringBuffer sb = new StringBuffer("");

            Map<String, List<String>> paramMap = command.getParamMap();
            if (paramMap.size() == 0 || (paramMap.size() == 1 && paramMap.get("-t") != null)) {//输出帮助信息
                sb.append(String.format("%16s", "-subscribeQ") + "\t处理订阅动作的队列\r\n").append(String.format("%16s", "-sendQ") + "\t服务器端往客户端推送的队列\r\n").append(String.format("%16s", "-resendQ") + "\t客户端重发时，服务器端往客户端推送的队列\r\n").append(String.format("%16s", "-publisherQ") + "\t客户端发到服务器端所有消息的队列，包括订阅的动作，发消息，重发消息，返回ack\r\n").append(String.format("%16s", "-publisherackQ") + "\t给发布者ack的队列\r\n").append(String.format("%16s", "-subscriberackQ") + "\t订阅者返回ack的队列\r\n").append(String.format("%16s", "-errorQ") + "\t发送时失败的队列\r\n").append(String.format("%16s", "-delQ") + "\t从数据库中拉取的消息发送成功后需要在数据库中删除的队列\r\n").append(String.format("%16s", "-checkerrorQ") + "\t多次连续重发失败后，需要入库的队列\r\n").append(String.format("%16s", "-checkQ") + "\t所有推送出去消息的队列\r\n").append(String.format("%16s", "-checkM") + "\t消息信息与时间的map，推送时put；订阅者返回ack时删除\r\n").append(String.format("%16s", "-dbcache") + "\t所有推送出的消息（包括新消息和数据库中获取的消息）信息与是否数据库标示的map。订阅者返回ack时，删除，另外，如果为db数据，加入db删除队列，即-delQ\r\n").append(String.format("%16s", "-messageCount") + "\t消息信息与重发次数的map\r\n");
                byte[] responseByte = sb.toString().getBytes("utf-8");
                event.getChannel().write(ChannelBuffers.copiedBuffer(responseByte));
            } else {
                String head = "";
                String data = "";
                for (String key : command.getParamMap().keySet()) {
                    if (!key.equals("-t")) {
                        head += String.format("%14s", key.replace("-", ""));
                        data += String.format("%14s", paramQueueMap.get(key));
                    }
                }

                head += "\r\n";
                data += "\r\n";

                sb.append(head).append(data);

                byte[] responseByte = sb.toString().getBytes("utf-8");
                event.getChannel().write(ChannelBuffers.copiedBuffer(responseByte));
                if (command.getParamMap().get("-t") != null) {
                    while (true) {
                        Thread.sleep(1000 * Integer.parseInt(command.getParamMap().get("-t").get(0)));

                        String data2 = "";
                        for (String key : command.getParamMap().keySet()) {
                            if (!key.equals("-t")) {
                                data2 += String.format("%14s", paramQueueMap.get(key));
                            }
                        }
                        data2 += "\r\n";
                        if (!event.getChannel().isOpen())
                            break;
                        event.getChannel().write(ChannelBuffers.copiedBuffer(data2.getBytes("utf-8")));
                    }
                }

            }

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
