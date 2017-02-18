package com.lemon.takinmq.broker.offset;

import com.lemon.takinmq.broker.BrokerStartUp;

/**
 * consumer消费进度管理
 * 只需保存每个consume对每个主题  每个文件消费的进度  
 * 在leveldb中进行保存     新建一个stats库
 * @author WangYazhou 
 * @date  2017年2月10日 下午12:20:20
 * @see   
 */
public class ConsumerOffsetManager {

    public transient BrokerStartUp brokerStartUp;

    public ConsumerOffsetManager() {
    }

    public ConsumerOffsetManager(BrokerStartUp brokerstartup) {
        this.brokerStartUp = brokerstartup;
    }

    //记录消费进度
    public void persist() {
        
    }
}
