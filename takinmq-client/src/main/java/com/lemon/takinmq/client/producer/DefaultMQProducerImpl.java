package com.lemon.takinmq.client.producer;

import com.lemon.takinmq.common.datainfo.SendMessageRequestHeader;
import com.lemon.takinmq.common.message.Message;
import com.lemon.takinmq.common.message.SendResult;
import com.lemon.takinmq.common.service.IBrokerService;
import com.lemon.takinmq.remoting.clientproxy.JDKProxy;
import com.lemon.takinmq.remoting.exception.MQBrokerException;
import com.lemon.takinmq.remoting.exception.MQClientException;
import com.lemon.takinmq.remoting.exception.RemotingException;
import com.lemon.takinmq.remoting.netty5.NettyClientConfig;
import com.lemon.takinmq.remoting.netty5.RemotingNettyClient;

public class DefaultMQProducerImpl {

    private final IBrokerService brokerService;
    private final RemotingNettyClient remotingclient;

    public DefaultMQProducerImpl(NettyClientConfig nettyClientConfig) {
        remotingclient = new RemotingNettyClient(nettyClientConfig);
        brokerService = new JDKProxy(remotingclient).createProxy(IBrokerService.class, IBrokerService.class.getName());
    }

    public void start() {
        remotingclient.start();
    }
    
    public void shutdown() {
        remotingclient.shutdown();
    }

    public SendResult send(Message msg, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException, Exception {
        SendMessageRequestHeader header = new SendMessageRequestHeader();
        brokerService.sendMessage(msg, header);
        return null;
    }

}
