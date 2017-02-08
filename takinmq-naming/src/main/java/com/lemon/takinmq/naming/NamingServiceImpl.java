package com.lemon.takinmq.naming;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.lemon.takinmq.common.datainfo.ClusterInfo;
import com.lemon.takinmq.common.datainfo.TopicConfigSerializeWrapper;
import com.lemon.takinmq.common.datainfo.TopicList;
import com.lemon.takinmq.common.datainfo.TopicRouteData;
import com.lemon.takinmq.common.service.INamingService;
import com.lemon.takinmq.naming.routeinfo.RouteInfoManager;

import io.netty.channel.Channel;

/**
 * 名称服务的接口实现类
 * 
 * @author WangYazhou
 * @date  2017年2月8日 下午4:53:04
 * @see
 */
public class NamingServiceImpl implements INamingService {

    private final RouteInfoManager routeInfoManager;

    public NamingServiceImpl() {
        routeInfoManager = new RouteInfoManager();
    }

    @Override
    public HashMap<String, String> register(String clustername, String address, String brokername, final long brokerId, String topic) throws Exception {
        TopicConfigSerializeWrapper topicConfigWrapper;//先默认按无来算
        topicConfigWrapper = new TopicConfigSerializeWrapper();
        topicConfigWrapper.getDataVersion().setCounter(new AtomicLong(0));
        topicConfigWrapper.getDataVersion().setTimestatmp(0);

        Channel channel = null;
        routeInfoManager.registerBroker(clustername, address, brokername, brokerId, "", topicConfigWrapper, channel);
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
