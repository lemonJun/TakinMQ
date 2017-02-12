package com.lemon.takinmq.broker.slave;

import com.lemon.takinmq.broker.BrokerStartUp;

/**
 * Slave从Master同步信息（非消息）
 *
 * @author WangYazhou
 * @date  2017年2月10日 下午2:35:40
 * @see
 */
public class SlaveSynchronize {
    private final BrokerStartUp brokerStartUp;

    private volatile String masterAddr = null;

    public SlaveSynchronize(BrokerStartUp brokerStartUp) {
        this.brokerStartUp = brokerStartUp;
    }

    public void syncAll() {
        this.syncTopicConfig();
        this.syncConsumerOffset();
        this.syncDelayOffset();
        this.syncSubscriptionGroupConfig();
    }

    public String getMasterAddr() {
        return masterAddr;
    }

    public void syncTopicConfig() {

    }

    public void syncConsumerOffset() {

    }

    public void syncDelayOffset() {

    }

    public void syncSubscriptionGroupConfig() {

    }

    public void setMasterAddr(String masterAddr) {
        this.masterAddr = masterAddr;
    }
}
