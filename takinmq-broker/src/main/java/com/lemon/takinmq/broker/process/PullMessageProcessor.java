package com.lemon.takinmq.broker.process;

import com.lemon.takinmq.broker.BrokerStartUp;

/**
 * 拉消息请求处理
 *
 * @author WangYazhou
 * @date  2017年2月10日 下午12:30:51
 * @see
 */
public class PullMessageProcessor {

    private final BrokerStartUp brokerStartUp;

    public PullMessageProcessor(BrokerStartUp brokerStartUp) {
        this.brokerStartUp = brokerStartUp;
    }

}
