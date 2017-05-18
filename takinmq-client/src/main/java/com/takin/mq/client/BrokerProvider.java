package com.takin.mq.client;

import com.takin.mq.broker.BrokerService;
import com.takin.rpc.client.ProxyFactory;

public class BrokerProvider {

    public static BrokerService getBrokerByTopic(String topic) {
        BrokerService producer = ProxyFactory.create(BrokerService.class, "broker", null, null);
        return producer;
    }

}
