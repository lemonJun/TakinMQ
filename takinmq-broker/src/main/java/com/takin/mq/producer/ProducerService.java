package com.takin.mq.producer;

import java.util.List;

import com.takin.mq.message.SimpleSendData;
import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface ProducerService {

    /**
     * 发送消息数据
     * @param data
     * @return
     * @throws Exception
     */
    public abstract long send(SimpleSendData data) throws Exception;

    /**
    * 指定要发送的消息所在的分区
    * @param data
    * @param partition  此分区应该小于初始化topic时指定的分区值
    * @return
    * @throws Exception
    */
    public abstract long send(SimpleSendData data, Integer partition) throws Exception;

    /**
     * 一次发送多条数据
     * @param datas
     * @return
     * @throws Exception
     */
    public abstract long send(List<SimpleSendData> datas) throws Exception;

    /**
     * 指定要发送的消息所在的分区
     * @param data
     * @param partition  此分区应该小于初始化topic时指定的分区值
     * @return
     * @throws Exception
     */
    public abstract long send(List<SimpleSendData> datas, int partition) throws Exception;

}