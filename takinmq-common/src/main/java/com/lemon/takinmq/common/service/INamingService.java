package com.lemon.takinmq.common.service;

import java.util.HashMap;

import com.lemon.takinmq.common.datainfo.ClusterInfo;
import com.lemon.takinmq.common.datainfo.TopicList;
import com.lemon.takinmq.common.datainfo.TopicRouteData;

/**
 * 名称服务的接口类
 * 
 * @author WangYazhou
 * @date  2017年2月7日 下午1:36:09
 * @see
 */
public interface INamingService {

    /**
     * 注册一个 broker 
     * @param address
     * @param topic
     * @return  kv配置信息
     * @throws Exception
     */
    public abstract HashMap<String, String> register(String address, String topic) throws Exception;

    /**
     * 取消注册
     * @param address
     * @param topic
     * @return  
     * @throws Exception
     */
    public abstract boolean unregister(String address, String topic) throws Exception;

    /**
     * 获取一个topic下的所有broker路由信息
     * @param topic
     * @return
     * @throws Exception
     */
    public abstract TopicRouteData getRouteInfoByTopic(String topic) throws Exception;

    /**
     * 获取所有集群信息
     * @param topic
     * @return
     * @throws Exception
     */
    public abstract ClusterInfo getBrokerClusterInfo(String topic) throws Exception;

    /**
     * 所取此集群下所有的主题
     * @return
     * @throws Exception
     */
    public abstract TopicList getAllTopicListFromNameserver() throws Exception;

    /**
     * 删除此集群下的某个主题
     * @param topic
     * @return
     * @throws Exception
     */
    public abstract boolean deleteTopicInNamesrv(String topic) throws Exception;

    /**
     * 
     * @param cluster
     * @return
     * @throws Exception
     */
    public abstract TopicList getTopicsByCluster(String cluster) throws Exception;

    /**
     * 
     * @param cluster
     * @return
     * @throws Exception
     */
    public abstract TopicList getSystemTopicListFromNs() throws Exception;

    /**
     * 增加一个kv
     * @param key
     * @param value
     * @throws Exception
     */
    public abstract void putkv(String key, String value) throws Exception;

}
