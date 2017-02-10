package com.lemon.takinmq.broker.latency;

import com.lemon.takinmq.broker.BrokerStartUp;

/**
 * 快速失败处理机制
 *
 * @author WangYazhou
 * @date  2017年2月10日 下午2:44:33
 * @see
 */
public class BrokerFastFailure {
    private final BrokerStartUp brokerStartUp;

    public BrokerFastFailure(BrokerStartUp brokerStartUp) {
        this.brokerStartUp = brokerStartUp;
    }

}
