package com.lemon.takinmq.broker.subscription;

import com.lemon.takinmq.broker.BrokerStartUp;

/**
 * 用来管理订阅组，包括订阅权限等
 *
 * @author WangYazhou
 * @date  2017年2月10日 下午2:31:55
 * @see
 */
public class SubscriptionGroupManager {

    private final BrokerStartUp brokerStartUp;

    public SubscriptionGroupManager(BrokerStartUp brokerStartUp) {
        this.brokerStartUp = brokerStartUp;
    }

    public void disableConsume(final String clusterName) {

    }
}
