package com.lemon.takinmq.store.stat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.common.ThreadFactoryImpl;
import com.lemon.takinmq.common.stat.MomentStatsItemSet;

public class BrokerStatsManager {

    private static final Logger logger = LoggerFactory.getLogger(BrokerStatsManager.class);

    private final String clusterName;

    //一个消息的限制大小
    public static final double SIZE_PER_COUNT = 64 * 1024;
    public static final String GROUP_GET_FALL_SIZE = "GROUP_GET_FALL_SIZE";
    public static final String GROUP_GET_FALL_TIME = "GROUP_GET_FALL_TIME";
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("BrokerStatsThread"));

    public BrokerStatsManager(String clusterName) {
        this.clusterName = clusterName;
    }

    private final MomentStatsItemSet momentStatsItemSetFallSize = new MomentStatsItemSet(GROUP_GET_FALL_SIZE, scheduledExecutorService, logger);
    private final MomentStatsItemSet momentStatsItemSetFallTime = new MomentStatsItemSet(GROUP_GET_FALL_TIME, scheduledExecutorService, logger);

    public MomentStatsItemSet getMomentStatsItemSetFallSize() {
        return momentStatsItemSetFallSize;
    }

    public MomentStatsItemSet getMomentStatsItemSetFallTime() {
        return momentStatsItemSetFallTime;
    }
}
