package com.lemon.takinmq.client;

import com.lemon.takinmq.common.message.MessageExt;
import com.lemon.takinmq.common.message.MessageQueue;
import com.lemon.takinmq.common.message.QueryResult;
import com.lemon.takinmq.remoting.exception.MQBrokerException;
import com.lemon.takinmq.remoting.exception.MQClientException;
import com.lemon.takinmq.remoting.exception.RemotingException;

public interface MQAdmin {

    /**
     * Creates an topic
     *
     * @param key
     *         accesskey
     * @param newTopic
     *         topic name
     * @param queueNum
     *         topic's queue number
     *
     * @throws MQClientException
     */
    void createTopic(final String key, final String newTopic, final int queueNum) throws MQClientException;

    /**
     * Creates an topic
     *
     * @param key
     *         accesskey
     * @param newTopic
     *         topic name
     * @param queueNum
     *         topic's queue number
     * @param topicSysFlag
     *         topic system flag
     *
     * @throws MQClientException
     */
    void createTopic(String key, String newTopic, int queueNum, int topicSysFlag) throws MQClientException;

    /**
     * Gets the message queue offset according to some time in milliseconds<br>
     * be cautious to call because of more IO overhead
     *
     * @param mq
     *         Instance of MessageQueue
     * @param timestamp
     *         from when in milliseconds.
     *
     * @return offset
     *
     * @throws MQClientException
     */
    long searchOffset(final MessageQueue mq, final long timestamp) throws MQClientException;

    /**
     * Gets the max offset
     *
     * @param mq
     *         Instance of MessageQueue
     *
     * @return the max offset
     *
     * @throws MQClientException
     */
    long maxOffset(final MessageQueue mq) throws MQClientException;

    /**
     * Gets the minimum offset
     *
     * @param mq
     *         Instance of MessageQueue
     *
     * @return the minimum offset
     *
     * @throws MQClientException
     */
    long minOffset(final MessageQueue mq) throws MQClientException;

    /**
     * Gets the earliest stored message time
     *
     * @param mq
     *         Instance of MessageQueue
     *
     * @return the time in microseconds
     *
     * @throws MQClientException
     */
    long earliestMsgStoreTime(final MessageQueue mq) throws MQClientException;

    /**
     * Query message according tto message id
     *
     * @param offsetMsgId
     *         message id
     *
     * @return message
     *
     * @throws InterruptedException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws MQClientException
     */
    MessageExt viewMessage(final String offsetMsgId) throws RemotingException, MQBrokerException, InterruptedException, MQClientException;

    /**
     * Query messages
     *
     * @param topic
     *         message topic
     * @param key
     *         message key index word
     * @param maxNum
     *         max message number
     * @param begin
     *         from when
     * @param end
     *         to when
     *
     * @return Instance of QueryResult
     *
     * @throws MQClientException
     * @throws InterruptedException
     */
    QueryResult queryMessage(final String topic, final String key, final int maxNum, final long begin, final long end) throws MQClientException, InterruptedException;

    /**
    
     * @param topic
     * @param msgId
     * @return The {@code MessageExt} of given msgId
     * @throws RemotingException
     * @throws MQBrokerException
     * @throws InterruptedException
     * @throws MQClientException
     */
    MessageExt viewMessage(String topic, String msgId) throws RemotingException, MQBrokerException, InterruptedException, MQClientException;

}
