package com.lemon.takinmq.client.consumer;

import java.util.Set;

import com.lemon.takinmq.common.message.MessageExt;
import com.lemon.takinmq.common.message.MessageQueue;
import com.lemon.takinmq.common.message.PullCallback;
import com.lemon.takinmq.common.message.PullResult;
import com.lemon.takinmq.remoting.exception.MQBrokerException;
import com.lemon.takinmq.remoting.exception.MQClientException;
import com.lemon.takinmq.remoting.exception.RemotingException;
import com.lemon.takinmq.remoting.netty5.NettyClientConfig;

public class DefaultMQPullConsumer implements MQPullConsumer {

    private final String consumerGroup;

    /**
     * Long polling mode, the Consumer connection max suspend time, it is not
     * recommended to modify
     */
    private long brokerSuspendMaxTimeMillis = 1000 * 20;
    /**
     * Long polling mode, the Consumer connection timeout(must greater than
     * brokerSuspendMaxTimeMillis), it is not recommended to modify
     */
    private long consumerTimeoutMillisWhenSuspend = 1000 * 30;
    /**
     * The socket timeout in milliseconds
     */
    private long consumerPullTimeoutMillis = 1000 * 10;

    private final DefaultMQPullConsumerImpl defaultMQPullConsumerImpl;

    public DefaultMQPullConsumer(final String consumerGroup, final NettyClientConfig clientConfig) {
        this.consumerGroup = consumerGroup;
        this.defaultMQPullConsumerImpl = new DefaultMQPullConsumerImpl(this, clientConfig);
    }

    @Override
    public void sendMessageBack(MessageExt msg, int delayLevel, String brokerName) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {

    }

    @Override
    public Set<MessageQueue> fetchSubscribeMessageQueues(String topic) throws MQClientException {
        return null;
    }

    @Override
    public void start() throws MQClientException {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void registerMessageQueueListener(String topic, MessageQueueListener listener) {

    }
    
    @Override 
    public PullResult pull(MessageQueue mq, String subExpression, long offset, int maxNums) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return this.defaultMQPullConsumerImpl.pull(mq, subExpression, offset, maxNums);
    }

    @Override
    public PullResult pull(MessageQueue mq, String subExpression, long offset, int maxNums, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return pullBlockIfNotFound(mq, subExpression, offset, maxNums);
    }

    @Override
    public void pull(MessageQueue mq, String subExpression, long offset, int maxNums, PullCallback pullCallback) throws MQClientException, RemotingException, InterruptedException {

    }

    @Override
    public void pull(MessageQueue mq, String subExpression, long offset, int maxNums, PullCallback pullCallback, long timeout) throws MQClientException, RemotingException, InterruptedException {

    }

    @Override
    public PullResult pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return null;
    }

    @Override
    public void pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums, PullCallback pullCallback) throws MQClientException, RemotingException, InterruptedException {

    }

    @Override
    public void updateConsumeOffset(MessageQueue mq, long offset) throws MQClientException {

    }

    @Override
    public long fetchConsumeOffset(MessageQueue mq, boolean fromStore) throws MQClientException {
        return 0;
    }

    @Override
    public Set<MessageQueue> fetchMessageQueuesInBalance(String topic) throws MQClientException {
        return null;
    }

    @Override
    public void sendMessageBack(MessageExt msg, int delayLevel, String brokerName, String consumerGroup) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {

    }

    public long getBrokerSuspendMaxTimeMillis() {
        return brokerSuspendMaxTimeMillis;
    }

    public void setBrokerSuspendMaxTimeMillis(long brokerSuspendMaxTimeMillis) {
        this.brokerSuspendMaxTimeMillis = brokerSuspendMaxTimeMillis;
    }

    public long getConsumerTimeoutMillisWhenSuspend() {
        return consumerTimeoutMillisWhenSuspend;
    }

    public void setConsumerTimeoutMillisWhenSuspend(long consumerTimeoutMillisWhenSuspend) {
        this.consumerTimeoutMillisWhenSuspend = consumerTimeoutMillisWhenSuspend;
    }

    public long getConsumerPullTimeoutMillis() {
        return consumerPullTimeoutMillis;
    }

    public void setConsumerPullTimeoutMillis(long consumerPullTimeoutMillis) {
        this.consumerPullTimeoutMillis = consumerPullTimeoutMillis;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

}
