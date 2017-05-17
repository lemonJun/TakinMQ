package com.takin.mq.consumer;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.takin.mq.message.MessageAndOffset;
import com.takin.mq.message.MessageSet;
import com.takin.mq.message.SimpleFetchData;
import com.takin.mq.store.ILog;
import com.takin.mq.store.LogManager;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl
public class FetchServiceImpl implements FetchService {

    private static final Logger logger = LoggerFactory.getLogger(FetchServiceImpl.class);

    @Override
    public SimpleFetchData fetch(String topic, long offset) throws Exception {
        int partition = GuiceDI.getInstance(LogManager.class).choosePartition(topic);
        return fetch(topic, offset, partition);
    }

    @Override
    public SimpleFetchData fetch(String topic, long offset, int partition) throws Exception {
        try {
            ILog log = GuiceDI.getInstance(LogManager.class).getOrCreateLog(topic, partition);
            MessageSet messageset = log.read(offset, 1);
            logger.info(JSON.toJSONString(messageset));
            Iterator<MessageAndOffset> ite = messageset.iterator();
            MessageAndOffset one = ite.next();
            SimpleFetchData data = new SimpleFetchData();
            data.setTopic(topic);
            data.setPartition(partition);
            data.setOffset(one.offset);
            data.setData(one.message.payload().toString());
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
