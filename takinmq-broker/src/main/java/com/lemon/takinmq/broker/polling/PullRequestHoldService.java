package com.lemon.takinmq.broker.polling;

import com.lemon.takinmq.broker.BrokerStartUp;

/**
 *  拉消息请求管理  如果拉不到消息  则在这里hold住
 *
 * @author WangYazhou
 * @date  2017年2月10日 下午12:31:13
 * @see
 */
public class PullRequestHoldService {

    private final BrokerStartUp brokerStartUp;

    public PullRequestHoldService(BrokerStartUp brokerStartUp) {
        this.brokerStartUp = brokerStartUp;
    }

    public void notifyMessageArriving(final String topic, final int queueId, final long maxOffset, final long tagsCode) {
    }

}
