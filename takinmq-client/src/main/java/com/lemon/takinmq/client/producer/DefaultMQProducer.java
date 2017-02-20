package com.lemon.takinmq.client.producer;

import java.util.List;

import com.lemon.takinmq.common.message.Message;
import com.lemon.takinmq.common.message.MessageQueue;
import com.lemon.takinmq.common.message.SendResult;
import com.lemon.takinmq.remoting.exception.MQBrokerException;
import com.lemon.takinmq.remoting.exception.MQClientException;
import com.lemon.takinmq.remoting.exception.RemotingException;

public class DefaultMQProducer implements MQProducer {

    @Override
    public void start() throws Exception {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<MessageQueue> fetchPublishMessageQueues(String topic) throws MQClientException {
        return null;
    }

    @Override
    public SendResult send(Message msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return null;
    }

    @Override
    public SendResult send(Message msg, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return null;
    }

}
