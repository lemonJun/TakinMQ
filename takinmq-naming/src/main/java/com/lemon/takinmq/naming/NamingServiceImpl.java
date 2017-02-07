package com.lemon.takinmq.naming;

import java.util.List;

import com.lemon.takinmq.common.service.INamingService;

public class NamingServiceImpl implements INamingService {

    @Override
    public boolean register(String address, String topic) throws Exception {
        return false;
    }

    @Override
    public boolean unregister(String address, String topic) throws Exception {
        return false;
    }

    @Override
    public List<String> getBrokerByTopic(String topic) throws Exception {
        return null;
    }

}
