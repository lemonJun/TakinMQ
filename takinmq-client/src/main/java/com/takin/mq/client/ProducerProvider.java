package com.takin.mq.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.takin.mq.producer.ProducerService;
import com.takin.rpc.client.ProxyFactory;

public class ProducerProvider {
    
    private static final ConcurrentHashMap<String, ProducerService> map = new ConcurrentHashMap<String, ProducerService>();

    private static final Lock lock = new ReentrantLock();

    public static ProducerService getProducerByTopic(String topic) {
        ProducerService producer = map.get(topic);
        if (producer == null) {
            lock.lock();
            try {
                producer = ProxyFactory.create(ProducerService.class, topic, null, null);
                map.put(topic, producer);
            } catch (Exception e) {
            } finally {
                lock.unlock();
            }
        }
        return producer;
    }

}
