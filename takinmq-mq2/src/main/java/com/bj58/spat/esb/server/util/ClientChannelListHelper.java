package com.bj58.spat.esb.server.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.Channel;

import com.bj58.spat.esb.server.communication.ESBContext;
import com.bj58.spat.esb.server.communication.TcpServer;
import com.bj58.spat.esb.server.config.Client;
import com.bj58.spat.esb.server.config.Subject;
import com.bj58.spat.esb.server.config.SubjectFactory;
import com.bj58.spat.esb.server.protocol.ESBSubject;

public class ClientChannelListHelper {
    private static final SubjectFactory sf = SubjectFactory.getSubjectFactory();
    private static final Object locker = new Object();
    private static final Log logger = LogFactory.getLog(ClientChannelListHelper.class);
    private static final Log subdesubLog = LogFactory.getLog("subAndUnsubLog");

    /**
     * 订阅
     * @param subList
     * @param ctx
     * @return
     * @throws Exception
     */
    public static boolean subscribe(List<ESBSubject> subList, ESBContext ctx) throws Exception {
        boolean rst = false;
        synchronized (locker) {
            for (ESBSubject sub : subList) {
                List<Client> clientList = SubjectFactory.getSubjectFactory().getClientList(sub.getSubjectID());
                if (clientList == null) {
                    logger.info("Service not have this subject ID [" + sub.getSubjectID() + "]");
                    continue;
                }

                for (Client client : clientList) {
                    if (client.getClientID() == sub.getClientID()) {
                        boolean exist = false;
                        List<Channel> channelList = client.getChannelList();
                        SocketAddress remoteAddr = ctx.getChannel().getRemoteAddress();
                        for (Channel channel : channelList) {
                            if (channel.getRemoteAddress().toString().equalsIgnoreCase(remoteAddr.toString())) {
                                exist = true;
                            }
                        }
                        if (!exist) {
                            rst = true;
                            channelList.add(ctx.getChannel());
                            logger.info("new subscriber connected clientID:" + sub.getClientID() + " addr:" + remoteAddr);
                        }
                    }
                }
            }
        }
        return rst;
    }

    public static void removeChannelList(Client client, Channel esbChannel) {
        synchronized (locker) {
            client.getChannelList().remove(esbChannel);
            esbChannel.close();
            TcpServer.allChannels.remove(esbChannel);
        }
    }

    public static void removeChannelListWithClose(Channel channel) {
        //ConcurrentHashMap<Integer, List<Client>> subjectMap = sf.getSubjectMap();
        ConcurrentHashMap<Integer, Subject> subjectMap = sf.getSubjectMap();
        for (Integer subjectID : subjectMap.keySet()) {
            synchronized (locker) {
                List<Client> clientlist = subjectMap.get(subjectID).getClientList();
                for (Client client : clientlist) {
                    List<Channel> esbchannellist = client.getChannelList();
                    for (Channel esbchannel : esbchannellist) {
                        if (esbchannel == channel) {
                            if (esbchannel != null && !esbchannel.isOpen()) {
                                try {
                                    InetAddress addr = ((InetSocketAddress) esbchannel.getRemoteAddress()).getAddress();
                                    subdesubLog.info("\t" + addr.getHostAddress() + "\t" + "clientID :" + client.getClientID() + "\tsubjectId :" + subjectID);
                                    client.getChannelList().remove(esbchannel);
                                    TcpServer.allChannels.remove(esbchannel);
                                    esbchannel.close();
                                } catch (Exception e) {
                                    logger.error("Class:ClientChannelListHelper Method:removeChannelListWithClose " + e.getMessage());
                                    continue;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
