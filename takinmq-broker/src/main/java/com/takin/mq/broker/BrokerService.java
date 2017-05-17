package com.takin.mq.broker;

import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface BrokerService {

    public boolean createTopic(String topic, int partition) throws Exception;

    public boolean deleteTopic(String topic) throws Exception;

    public int getTopicConfig(String topic) throws Exception;

}
