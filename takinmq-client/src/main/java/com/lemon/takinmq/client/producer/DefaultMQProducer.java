package com.lemon.takinmq.client.producer;

import java.util.List;

import com.lemon.takinmq.common.MixAll;
import com.lemon.takinmq.common.message.Message;
import com.lemon.takinmq.common.message.MessageQueue;
import com.lemon.takinmq.common.message.SendResult;
import com.lemon.takinmq.remoting.exception.MQBrokerException;
import com.lemon.takinmq.remoting.exception.MQClientException;
import com.lemon.takinmq.remoting.exception.RemotingException;
import com.lemon.takinmq.remoting.netty5.NettyClientConfig;

public class DefaultMQProducer implements MQProducer {

    private String producerGroup;
    /**
     * Just for testing or demo program
     */
    private String createTopicKey = MixAll.DEFAULT_TOPIC;
    private volatile int defaultTopicQueueNums = 4;
    private int sendMsgTimeout = 3000;
    private int compressMsgBodyOverHowmuch = 1024 * 4;
    private int retryTimesWhenSendFailed = 2;//发送失败  重发送的次数
    private int retryTimesWhenSendAsyncFailed = 2;

    private boolean retryAnotherBrokerWhenNotStoreOK = false;
    private int maxMessageSize = 1024 * 1024 * 4; // 4M

    private final long time_out = 3 * 1000;

    private final DefaultMQProducerImpl defaultMQProducerImpl;

    public DefaultMQProducer(NettyClientConfig nettyClientConfig) {
        this.defaultMQProducerImpl = new DefaultMQProducerImpl(nettyClientConfig);
    }

    @Override
    public void start() throws Exception {
        defaultMQProducerImpl.start();
    }

    @Override
    public void shutdown() {
        defaultMQProducerImpl.shutdown();
    }

    @Override
    public List<MessageQueue> fetchPublishMessageQueues(String topic) throws MQClientException {
        return null;
    }

    @Override
    public SendResult send(Message msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException, Exception {
        return defaultMQProducerImpl.send(msg, time_out);
    }

    @Override
    public SendResult send(Message msg, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return null;
    }

}
