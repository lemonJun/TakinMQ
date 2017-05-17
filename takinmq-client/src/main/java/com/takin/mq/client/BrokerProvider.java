package com.takin.mq.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.takin.mq.broker.BrokerService;
import com.takin.rpc.client.ProxyFactory;

public class BrokerProvider {
    private static final ConcurrentHashMap<String, BrokerService> map = new ConcurrentHashMap<String, BrokerService>();

    private static final Lock lock = new ReentrantLock();

    public static BrokerService getBrokerByTopic(String topic) {
        BrokerService producer = map.get(topic);
        if (producer == null) {
            lock.lock();
            try {
                producer = ProxyFactory.create(BrokerService.class, topic, null, null);
                map.put(topic, producer);
            } catch (Exception e) {
            } finally {
                lock.unlock();
            }
        }
        return producer;
    }

}
