package com.lemon.takinmq.naming;

import java.util.concurrent.atomic.AtomicLong;

import com.lemon.takinmq.common.datainfo.ClusterInfo;
import com.lemon.takinmq.common.datainfo.TopicConfigSerializeWrapper;
import com.lemon.takinmq.common.datainfo.TopicList;
import com.lemon.takinmq.common.datainfo.TopicRouteData;
import com.lemon.takinmq.common.naming.RegisterBrokerResult;
import com.lemon.takinmq.common.service.INamingService;
import com.lemon.takinmq.naming.routeinfo.RouteInfoManager;
import com.lemon.takinmq.remoting.GlobalContext;

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
    public RegisterBrokerResult register(String clustername, String address, String brokername, final long brokerId, TopicConfigSerializeWrapper topicWrapper) throws Exception {
        TopicConfigSerializeWrapper topicConfigWrapper;
        if (topicWrapper != null) {
            topicConfigWrapper = topicWrapper;
        } else {
            topicConfigWrapper = new TopicConfigSerializeWrapper();
            topicConfigWrapper.getDataVersion().setCounter(new AtomicLong(0));
            topicConfigWrapper.getDataVersion().setTimestatmp(0);
        }

        Channel channel = GlobalContext.getSingleton().getFromThreadLocal().getContext().channel();
        RegisterBrokerResult brokerResult = routeInfoManager.registerBroker(clustername, address, brokername, brokerId, "", topicConfigWrapper, channel);
        return brokerResult;

    }

    @Override
    public boolean unregister(String clustername, String brokerAddr, String brokername, final long brokerId) throws Exception {
        routeInfoManager.unregisterBroker(clustername, brokerAddr, brokername, brokerId);
        return true;
    }

    @Override
    public TopicRouteData getRouteInfoByTopic(String topic) throws Exception {
        TopicRouteData routedata = routeInfoManager.pickupTopicRouteData(topic);
        return routedata;
    }

    @Override
    public ClusterInfo getBrokerClusterInfo(String topic) throws Exception {
        ClusterInfo clusterInfo = routeInfoManager.getAllClusterInfo();
        return clusterInfo;
    }

    @Override
    public TopicList getAllTopicListFromNameserver() throws Exception {
        TopicList toplicList = routeInfoManager.getAllTopicList();
        return toplicList;
    }

    @Override
    public boolean deleteTopicInNamesrv(String topic) throws Exception {
        routeInfoManager.deleteTopic(topic);
        return true;
    }

    @Override
    public TopicList getTopicsByCluster(String cluster) throws Exception {
        TopicList toplicList = routeInfoManager.getTopicsByCluster(cluster);
        return toplicList;
    }

    @Override
    public TopicList getSystemTopicListFromNs() throws Exception {
        TopicList toplicList = routeInfoManager.getSystemTopicList();
        return toplicList;
    }

    @Override
    public void putkv(String namespace, String key, String value) throws Exception {

    }

}
