package com.lemon.takinmq.broker.topic;

import com.lemon.takinmq.broker.BrokerStartUp;

/**
 * topic配置管理
 *
 * @author WangYazhou
 * @date  2017年2月10日 下午12:24:27
 * @see
 */
public class TopicConfigManager {

    private transient BrokerStartUp brokerStartUp;

    public TopicConfigManager() {

    }

    public TopicConfigManager(BrokerStartUp brokerStartUp) {
        this.brokerStartUp = brokerStartUp;
    }

}
