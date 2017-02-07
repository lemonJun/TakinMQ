package com.lemon.takinmq.common.service;

import java.util.List;

/**
 * 服务发现的接口类
 * 
 * @author WangYazhou
 * @date  2017年2月7日 下午1:36:09
 * @see
 */
public interface INamingService {

    //注册一个 broker
    public abstract boolean register(String address, String topic) throws Exception;

    //取消注册
    public abstract boolean unregister(String address, String topic) throws Exception;

    //获取一个topic下的所有broker
    public abstract List<String> getBrokerByTopic(String topic) throws Exception;

}
