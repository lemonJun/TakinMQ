package com.takin.mq.client;

import com.takin.mq.producer.ProducerService;
import com.takin.rpc.client.ProxyFactory;

public class ProducerProvider {

    public static ProducerService getProducerByTopic() {
        ProducerService producer = ProxyFactory.create(ProducerService.class, "broker", null, null);
        return producer;
    }
}
