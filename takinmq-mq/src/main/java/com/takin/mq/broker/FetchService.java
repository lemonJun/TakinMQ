
package com.takin.mq.broker;

import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface FetchService {

    /**
     * 
     * 获取消息
     * @param data
     * @return
     * @throws Exception
     */
    public abstract StringProducerData fetch(String topic, int offset) throws Exception;

    /**
     * 
     * 
    * 指定分区下获取消息 
    * @param data
    * @param partition  此分区应该小于初始化topic时指定的分区值
    * @return 
    * @throws Exception
    */
    public abstract StringProducerData fetch(String topic, int partition, int offset) throws Exception;

}
