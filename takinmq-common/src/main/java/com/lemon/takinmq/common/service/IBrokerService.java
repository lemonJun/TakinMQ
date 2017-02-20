package com.lemon.takinmq.common.service;

import com.lemon.takinmq.common.anno.ImplementBy;
import com.lemon.takinmq.common.datainfo.PutMessageResult;
import com.lemon.takinmq.common.datainfo.SendMessageRequestHeader;
import com.lemon.takinmq.common.message.Message;

/**
 * broker主要实现的功能
 *
 * @author WangYazhou
 * @date  2017年2月18日 下午6:37:05
 * @see
 */
@ImplementBy(implclass = "com.lemon.takinmq.broker.BrokerServiceImpl")
public interface IBrokerService {

    /**
     * 发送一个消息
     * @param message
     * @param requestHeader
     * @return
     * @throws Exception
     */
    public abstract PutMessageResult sendMessage(Message message, SendMessageRequestHeader requestHeader) throws Exception;

    /**
     * 客户端发送的消息消费确认
     * @throws Exception
     */
    public abstract void consumerSendMsgBack() throws Exception;

    /**
     * 查找消息
     * @param msgid
     * @throws Exception
     */
    public abstract void queryMessage(String msgid) throws Exception;

    /**
     * 拉取消息并消费
     * @param topic
     * @throws Exception
     */
    public abstract void pullMessage(String topic, boolean needAck) throws Exception;

    /**
     * 获取一个组下的消费者
     * @param group
     * @throws Exception
     */
    public abstract void getConsumerListByGroup(String group) throws Exception;

    /**
     * 查看一个客户端的消费进度
     * @param topic
     * @param client
     * 
     * @throws Exception
     */
    public abstract void queryConsumerOffset(String topic, String client) throws Exception;

    /**
     * 注册一个客户端
     * @param topic
     * @param client
     * @throws Exception
     */
    public abstract void registerClient(String topic, String client) throws Exception;

    /**
     * 取消一个客户端
     * @param topic
     * @param client
     * @throws Exception
     */
    public abstract void unregisterClient(String topic, String client) throws Exception;

    /**------------以下主要是为维护使用------------*/
    /**
     * 生成一个主题  
     * @param topic
     * @throws Exception
     */
    public abstract void createTopic(String topic) throws Exception;

    /**
     * 获取所有的主题
     * @param topic
     * @throws Exception
     */
    public abstract void getAllTopicConfig(String topic) throws Exception;

}
