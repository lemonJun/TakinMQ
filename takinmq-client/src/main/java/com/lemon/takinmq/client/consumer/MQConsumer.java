package com.lemon.takinmq.client.consumer;

import java.util.Set;

import com.lemon.takinmq.common.message.MessageExt;
import com.lemon.takinmq.common.message.MessageQueue;
import com.lemon.takinmq.remoting.exception.MQBrokerException;
import com.lemon.takinmq.remoting.exception.MQClientException;
import com.lemon.takinmq.remoting.exception.RemotingException;

public interface MQConsumer {
    /**
     * If consuming failure,message will be send back to the broker,and delay consuming some time
     *
     * @param msg
     * @param delayLevel
     * @param brokerName
     *
     * @throws RemotingException
     * @throws MQBrokerException
     * @throws InterruptedException
     * @throws MQClientException
     */
    void sendMessageBack(final MessageExt msg, final int delayLevel, final String brokerName) throws RemotingException, MQBrokerException, InterruptedException, MQClientException;

    /**
     * Fetch message queues from consumer cache according to the topic
     *
     * @param topic
     *         message topic
     *
     * @return queue set
     *
     * @throws MQClientException
     */
    Set<MessageQueue> fetchSubscribeMessageQueues(final String topic) throws MQClientException;
}
