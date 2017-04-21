package com.takin.mq.broker;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.takin.mq.log.ILog;
import com.takin.mq.log.LogManager;
import com.takin.mq.message.MessageAndOffset;
import com.takin.mq.message.MessageSet;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl
public class FetchServiceImpl implements FetchService {

    private static final Logger logger = LoggerFactory.getLogger(FetchServiceImpl.class);

    @Override
    public StringProducerData fetch(String topic, int offset) throws Exception {
        int partition = GuiceDI.getInstance(LogManager.class).choosePartition(topic);
        return fetch(topic, partition, offset);
    }

    @Override
    public StringProducerData fetch(String topic, int partition, int offset) throws Exception {
        ILog log = GuiceDI.getInstance(LogManager.class).getOrCreateLog(topic, partition);
        MessageSet messageset = log.read(offset, 1);
        logger.info(JSON.toJSONString(messageset));
        Iterator<MessageAndOffset> ite = messageset.iterator();
        MessageAndOffset one = ite.next();
        StringProducerData data = new StringProducerData();
        data.setTopic(topic);
        data.setData(one.message.payload().toString());
        return data;
    }

}
