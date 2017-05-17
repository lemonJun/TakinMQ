package com.takin.mq.broker;

import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface BrokerService {
    
    public boolean request(TopicCommand command) throws Exception;

}
