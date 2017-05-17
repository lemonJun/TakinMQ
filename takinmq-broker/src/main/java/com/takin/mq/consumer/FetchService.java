
package com.takin.mq.consumer;

import com.takin.mq.message.SimpleFetchData;
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
    public abstract SimpleFetchData fetch(String topic, long offset) throws Exception;

    /**
     * 
     * 
    * 指定分区下获取消息 
    * @param data
    * @param partition  此分区应该小于初始化topic时指定的分区值
    * @return 
    * @throws Exception
    */
    public abstract SimpleFetchData fetch(String topic, long offset, int partition) throws Exception;

}
