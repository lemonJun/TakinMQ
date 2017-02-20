package com.lemon.takinmq.client.producer;

import java.util.List;

import com.lemon.takinmq.common.message.Message;
import com.lemon.takinmq.common.message.MessageQueue;
import com.lemon.takinmq.common.message.SendResult;
import com.lemon.takinmq.remoting.exception.MQBrokerException;
import com.lemon.takinmq.remoting.exception.MQClientException;
import com.lemon.takinmq.remoting.exception.RemotingException;

public interface MQProducer {

    public abstract void start() throws Exception;

    public abstract void shutdown();

    public abstract List<MessageQueue> fetchPublishMessageQueues(final String topic) throws MQClientException;

    public abstract SendResult send(final Message msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException, Exception;

    public abstract SendResult send(final Message msg, final long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException;

    //    public abstract void send(final Message msg, final SendCallback sendCallback) throws MQClientException, RemotingException, InterruptedException;
    //
    //    public abstract void send(final Message msg, final SendCallback sendCallback, final long timeout) throws MQClientException, RemotingException, InterruptedException;
    //
    //    public abstract void sendOneway(final Message msg) throws MQClientException, RemotingException, InterruptedException;
    //    
    //    public abstract SendResult send(final Message msg, final MessageQueue mq) throws MQClientException, RemotingException, MQBrokerException, InterruptedException;
    //
    //    public abstract SendResult send(final Message msg, final MessageQueue mq, final long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException;
    //
    //    public abstract void send(final Message msg, final MessageQueue mq, final SendCallback sendCallback) throws MQClientException, RemotingException, InterruptedException;
    //
    //    public abstract void send(final Message msg, final MessageQueue mq, final SendCallback sendCallback, long timeout) throws MQClientException, RemotingException, InterruptedException;
    //
    //    public abstract void sendOneway(final Message msg, final MessageQueue mq) throws MQClientException, RemotingException, InterruptedException;
    //
    //    public abstract SendResult send(final Message msg, final MessageQueueSelector selector, final Object arg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException;
    //
    //    public abstract SendResult send(final Message msg, final MessageQueueSelector selector, final Object arg, final long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException;
    //
    //    public abstract void send(final Message msg, final MessageQueueSelector selector, final Object arg, final SendCallback sendCallback) throws MQClientException, RemotingException, InterruptedException;
    //
    //    public abstract void send(final Message msg, final MessageQueueSelector selector, final Object arg, final SendCallback sendCallback, final long timeout) throws MQClientException, RemotingException, InterruptedException;
    //
    //    public abstract void sendOneway(final Message msg, final MessageQueueSelector selector, final Object arg) throws MQClientException, RemotingException, InterruptedException;

    //    TransactionSendResult sendMessageInTransaction(final Message msg, final LocalTransactionExecuter tranExecuter, final Object arg) throws MQClientException;

}
