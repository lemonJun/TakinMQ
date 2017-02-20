package com.lemon.takinmq.client.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.common.message.MessageQueue;
import com.lemon.takinmq.common.message.PullMessageRequestHeader;
import com.lemon.takinmq.common.message.PullResult;
import com.lemon.takinmq.common.service.IBrokerService;
import com.lemon.takinmq.remoting.clientproxy.JDKProxy;
import com.lemon.takinmq.remoting.exception.MQBrokerException;
import com.lemon.takinmq.remoting.exception.MQClientException;
import com.lemon.takinmq.remoting.exception.RemotingException;
import com.lemon.takinmq.remoting.netty5.NettyClientConfig;
import com.lemon.takinmq.remoting.netty5.RemotingNettyClient;

public class DefaultMQPullConsumerImpl {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMQPullConsumerImpl.class);

    private final DefaultMQPullConsumer defaultMQPullConsumer;

    private final RemotingNettyClient remotingClient;

    private final IBrokerService brokerService;

    public DefaultMQPullConsumerImpl(final DefaultMQPullConsumer defaultMQPullConsumer, final NettyClientConfig nettyClientConfig) {
        this.defaultMQPullConsumer = defaultMQPullConsumer;
        this.remotingClient = new RemotingNettyClient(nettyClientConfig);
        brokerService = new JDKProxy(remotingClient).createProxy(IBrokerService.class, IBrokerService.class.getName());
    }

    public PullResult pull(MessageQueue mq, String subExpression, long offset, int maxNums) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return pull(mq, subExpression, offset, maxNums, this.defaultMQPullConsumer.getConsumerPullTimeoutMillis());
    }

    public PullResult pull(MessageQueue mq, String subExpression, long offset, int maxNums, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return this.pullSyncImpl(mq, subExpression, offset, maxNums, false, timeout);
    }

    //同步拉取消息 
    private PullResult pullSyncImpl(MessageQueue mq, String subExpression, long offset, int maxNums, boolean block, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        try {
            PullMessageRequestHeader requestHeader = new PullMessageRequestHeader();
            requestHeader.setConsumerGroup(this.defaultMQPullConsumer.getConsumerGroup());
            requestHeader.setTopic("Test");
            requestHeader.setQueueId(mq.getQueueId());
            requestHeader.setQueueOffset(offset);
            requestHeader.setMaxMsgNums(maxNums);
            requestHeader.setSysFlag(0);
            requestHeader.setCommitOffset(0);
            requestHeader.setSuspendTimeoutMillis(3000);
            requestHeader.setSubscription(subExpression);
            requestHeader.setSubVersion(1);

            PullResult result = brokerService.pullMessage(requestHeader);
            
            return result;
        } catch (Exception e) {
            logger.error("pull msg error", e);

        }
        return null;
    }

}
