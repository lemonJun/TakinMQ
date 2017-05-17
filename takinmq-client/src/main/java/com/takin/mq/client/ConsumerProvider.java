package com.takin.mq.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.takin.mq.consumer.FetchService;
import com.takin.mq.message.SimpleFetchData;
import com.takin.rpc.client.ProxyFactory;

public class ConsumerProvider {

    private static final ConcurrentHashMap<String, Manager> map = new ConcurrentHashMap<String, Manager>();

    private static final Lock lock = new ReentrantLock();

    public static void registTopicHandler(String topic, ReceiveHandler handler) {
        Manager manager = map.get(topic);
        if (manager == null) {
            lock.lock();
            try {
                FetchService fetch = ProxyFactory.create(FetchService.class, topic, null, null);
                int partition = BrokerProvider.getBrokerByTopic(topic).getTopicConfig(topic);
                manager = new Manager(topic, fetch, handler, partition);
                map.put(topic, manager);
            } catch (Exception e) {
            } finally {
                lock.unlock();
            }
        }
    }

    static class Manager {
        private final String topic;
        final private FetchService fetch;
        final private ReceiveHandler handler;
        private int partition;

        public Manager(String topic, FetchService fetch, ReceiveHandler handler, int partition) {
            this.topic = topic;
            this.fetch = fetch;
            this.handler = handler;
            this.partition = partition;

            Executors.newFixedThreadPool(1).submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        for (int i = 0; i < partition; i++) {
                            try {
                                long offset = OffSetManager.getOffetByTopic(topic, i);
                                SimpleFetchData fetchdata = fetch.fetch(topic, offset, i);
                                if (fetchdata != null) {
                                    OffSetManager.putOffetByTopic(topic, i, fetchdata.getOffset());
                                    handler.notify(fetchdata);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }

    }
}
