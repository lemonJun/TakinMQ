package com.takin.mq.producer;

import com.takin.mq.message.StringProducerData;
import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface ProducerService {

    /**
     * 发送消息数据
     * @param data
     * @return
     * @throws Exception
     */
    public abstract int send(StringProducerData data) throws Exception;

    /**
    * 指定要发送的消息所在的分区
    * @param data
    * @param partition  此分区应该小于初始化topic时指定的分区值
    * @return
    * @throws Exception
    */
    public abstract int send(StringProducerData data, int partition) throws Exception;

}
