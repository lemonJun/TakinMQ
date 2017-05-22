package com.takin.mq.store2;

import com.alibaba.fastjson.JSON;
import com.takin.mq.message.SimpleSendData;

public class DelayMessage {

    public static final String delay = "delay-topic";

    public static SimpleSendData turnToDelay(SimpleSendData simpledata) {
        SimpleSendData delaydata = new SimpleSendData();
        delaydata.setTopic(delay);
        delaydata.setData(JSON.toJSONString(simpledata));
        return delaydata;
    }

    public static SimpleSendData delayToSimple(SimpleSendData delayData) {
        SimpleSendData simpledata = JSON.parseObject(delayData.getData(), SimpleSendData.class);
        return simpledata;
    }
    
    

}
