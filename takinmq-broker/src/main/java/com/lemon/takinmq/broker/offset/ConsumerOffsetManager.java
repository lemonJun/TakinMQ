package com.lemon.takinmq.broker.offset;

import com.lemon.takinmq.broker.BrokerStartUp;

/**
 * consumer消费进度管理
 *
 * @author WangYazhou
 * @date  2017年2月10日 下午12:20:20
 * @see
 */
public class ConsumerOffsetManager {

    public transient BrokerStartUp brokerStartUp;

    public ConsumerOffsetManager() {
    }

    public ConsumerOffsetManager(BrokerStartUp brokerstartup) {
        this.brokerStartUp = brokerstartup;
    }

    //记录消费进度
    public void persist() {

    }
}
