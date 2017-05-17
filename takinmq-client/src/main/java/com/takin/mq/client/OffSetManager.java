package com.takin.mq.client;

import java.util.concurrent.ConcurrentHashMap;

public class OffSetManager {

    private static final ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<String, Long>();

    protected static long getOffetByTopic(String topic, int partition) {
        String key = String.format("%s_%s", topic, partition);
        Long offset = map.get(key);
        return offset == null ? 0 : offset.longValue();
    }

    protected static void putOffetByTopic(String topic, int partition, long offset) {
        String key = String.format("%s_%s", topic, partition);
        map.put(key, offset);
    }

}
