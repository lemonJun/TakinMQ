package com.lemon.takinmq.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 存储层统计服务
 *
 * @author WangYazhou
 * @date  2017年2月10日 下午6:29:15
 * @see
 */
public class StoreStatsService {

    private final AtomicLong getMessageTimesTotalFound = new AtomicLong(0);
    private final AtomicLong getMessageTransferedMsgCount = new AtomicLong(0);
    private final AtomicLong getMessageTimesTotalMiss = new AtomicLong(0);

    // putMessage，调用总数
    private final Map<String, AtomicLong> putMessageTopicTimesTotal = new ConcurrentHashMap<String, AtomicLong>(128);
    private final Map<String, AtomicLong> putMessageTopicSizeTotal = new ConcurrentHashMap<String, AtomicLong>(128);
    // getMessage，调用总数

    public AtomicLong getGetMessageTimesTotalFound() {
        return getMessageTimesTotalFound;
    }

    public AtomicLong getGetMessageTransferedMsgCount() {
        return getMessageTransferedMsgCount;
    }

    public AtomicLong getGetMessageTimesTotalMiss() {
        return getMessageTimesTotalMiss;
    }

    public long getPutMessageTimesTotal() {
        long rs = 0;
        for (AtomicLong data : putMessageTopicTimesTotal.values()) {
            rs += data.get();
        }
        return rs;
    }
}
