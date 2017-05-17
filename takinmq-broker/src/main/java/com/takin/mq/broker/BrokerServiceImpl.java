package com.takin.mq.broker;

import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl
public class BrokerServiceImpl implements BrokerService {

    @Override
    public boolean request(TopicCommand command) throws Exception {
        return false;
    }
    
}
