package com.lemon.takinmq.naming;

import java.util.HashMap;

import com.lemon.takinmq.common.datainfo.ClusterInfo;
import com.lemon.takinmq.common.datainfo.TopicList;
import com.lemon.takinmq.common.datainfo.TopicRouteData;
import com.lemon.takinmq.common.service.INamingService;

public class NamingServiceImpl implements INamingService {

    @Override
    public HashMap<String, String> register(String address, String topic) throws Exception {
        return null;
    }

    @Override
    public boolean unregister(String address, String topic) throws Exception {
        return false;
    }

    @Override
    public TopicRouteData getRouteInfoByTopic(String topic) throws Exception {
        return null;
    }

    @Override
    public ClusterInfo getBrokerClusterInfo(String topic) throws Exception {
        return null;
    }

    @Override
    public TopicList getAllTopicListFromNameserver() throws Exception {
        return null;
    }

    @Override
    public boolean deleteTopicInNamesrv(String topic) throws Exception {
        return false;
    }

    @Override
    public TopicList getTopicsByCluster(String cluster) throws Exception {
        return null;
    }

    @Override
    public TopicList getSystemTopicListFromNs() throws Exception {
        return null;
    }

    @Override
    public void putkv(String key, String value) throws Exception {

    }

}
