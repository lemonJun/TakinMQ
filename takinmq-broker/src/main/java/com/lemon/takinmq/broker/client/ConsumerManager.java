package com.lemon.takinmq.broker.client;

/**
 * 
 * 管理消费者
 *
 * @author WangYazhou
 * @date  2017年2月18日 下午4:37:42
 * @see
 */
public class ConsumerManager {

    private final ConsumerIdsChangeListener consumerIdsChangeListener;

    public ConsumerManager(final ConsumerIdsChangeListener consumerIdsChangeListener) {
        this.consumerIdsChangeListener = consumerIdsChangeListener;
    }

}
