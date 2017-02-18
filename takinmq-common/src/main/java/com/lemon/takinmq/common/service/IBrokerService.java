package com.lemon.takinmq.common.service;

import com.lemon.takinmq.common.datainfo.PutMessageResult;
import com.lemon.takinmq.common.datainfo.SendMessageRequestHeader;
import com.lemon.takinmq.common.datainfo.TopicConfigSerializeWrapper;
import com.lemon.takinmq.common.naming.RegisterBrokerResult;

public interface IBrokerService {

    public abstract RegisterBrokerResult register(String clustername, String brokeraddress, String brokername, Long brokerId, TopicConfigSerializeWrapper topic) throws Exception;

    public abstract PutMessageResult sendMessage(String message, SendMessageRequestHeader requestHeader) throws Exception;
    
}
