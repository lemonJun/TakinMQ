package com.lemon.takinmq.naming.routeinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.lemon.takinmq.common.datainfo.BrokerData;
import com.lemon.takinmq.common.datainfo.BrokerLiveInfo;
import com.lemon.takinmq.common.datainfo.QueueData;

/**
 * 服务发现服务的主类
 *
 * @author WangYazhou
 * @date  2017年2月7日 下午5:40:41
 * @see
 */
public class RouteInfoManager {
    private static final Logger logger = LoggerFactory.getLogger(RouteInfoManager.class);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final HashMap<String/* topic */, List<QueueData>> topicQueueTable;
    private final HashMap<String/* brokerName */, BrokerData> brokerAddrTable;
    private final HashMap<String/* clusterName */, Set<String/* brokerName */>> clusterAddrTable;
    private final HashMap<String/* brokerAddr */, BrokerLiveInfo> brokerLiveTable;
    private final HashMap<String/* brokerAddr */, List<String>/* Filter Server */> filterServerTable;

    public RouteInfoManager() {
        topicQueueTable = Maps.newHashMap();
        brokerAddrTable = Maps.newHashMap();
        clusterAddrTable = Maps.newHashMap();
        brokerLiveTable = Maps.newHashMap();
        filterServerTable = Maps.newHashMap();

    }

}
