package com.takin.mq.broker;

import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl
public class BrokerServiceImpl implements BrokerService {

    @Override
    public boolean createTopic(String topic, int partition) throws Exception {
        return false;
    }

    @Override
    public boolean deleteTopic(String topic) throws Exception {
        return false;
    }

    @Override
    public int getTopicConfig(String topic) throws Exception {
        return 3;
    }

}
