package com.lemon.takinmq.client.consumer;

import com.lemon.takinmq.remoting.exception.MQClientException;

public interface MQPushConsumer extends MQConsumer {

    /**
     * Start the consumer
     *
     * @throws MQClientException
     */
    void start() throws MQClientException;

    /**
     * Shutdown the consumer
     */
    void shutdown();

    void registerMessageListener(final MessageListenerConcurrently messageListener);

    void registerMessageListener(final MessageListenerOrderly messageListener);

    /**
     * 订阅消息，方法可以调用多次来订阅不同的Topic，也可覆盖之前Topic的订阅过滤表达式
     * 
     * @param topic
     *            消息主题
     * @param subExpression
     *            1、订阅过滤表达式字符串，broker依据此表达式进行过滤。目前只支持或运算<br>
     *            例如: "tag1 || tag2 || tag3"<br>
     *            如果subExpression等于null或者*，则表示全部订阅<br>
     * 
     *            2、高级过滤方式，传入一个Java程序，例如:
     *            "rocketmq.message.filter.cousumergroup.FilterClassName"<br>
     *            "rocketmq.message.filter.cousumergroup.topic1.FilterClassName"<br>
     *            注意事项：<br>
     *            a、Java程序必须继承于com.alibaba.rocketmq.common.filter.MessageFilter，
     *            并实现相应的接口来过滤<br>
     *            b、Java程序必须是UTF-8编码<br>
     *            c、这个Java过滤程序只能依赖JDK里的类，非JDK的Java类一律不能依赖
     *            d、过滤方法里不允许抛异常，只要抛异常，整个消费过程就停止
     *            e、FilterClassName.java文件放置到CLASSPATH目录下，例如src/main/resources
     * @param listener
     *            消息回调监听器
     * @throws MQClientException
     */
    void subscribe(final String topic, final String subExpression) throws MQClientException;

    /**
     * Subscribe some topic
     *
     * @param topic
     * @param fullClassName
     *         full class name,must extend
     *         com.alibaba.rocketmq.common.filter. MessageFilter
     * @param filterClassSource
     *         class source code,used UTF-8 file encoding,must be responsible
     *         for your code safety
     *
     * @throws MQClientException
     */
    void subscribe(final String topic, final String fullClassName, final String filterClassSource) throws MQClientException;

    /**
     * 取消订阅，从当前订阅组内注销，消息会被订阅组内其他订阅者订阅
     * 
     * @param topic
     *            消息主题
     */
    void unsubscribe(final String topic);

    // U动态调整消费线程池线程数量
    void updateCorePoolSize(int corePoolSize);

    //消费线程挂起，暂停消费
    void suspend();

    //消费线程恢复，继续消费
    void resume();
}
