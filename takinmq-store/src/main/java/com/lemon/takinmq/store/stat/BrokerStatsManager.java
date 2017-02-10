package com.lemon.takinmq.store.stat;

public class BrokerStatsManager {

    private final String clusterName;

    //一个消息的限制大小
    public static final double SIZE_PER_COUNT = 64 * 1024;

    public BrokerStatsManager(String clusterName) {
        this.clusterName = clusterName;
    }

}
