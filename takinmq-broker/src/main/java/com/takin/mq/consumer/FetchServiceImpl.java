package com.takin.mq.consumer;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.takin.emmet.collection.CollectionUtil;
import com.takin.mq.message.Message;
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
    public SimpleFetchData fetch(String topic, Long offset) throws Exception {
        int partition = GuiceDI.getInstance(LogManager.class).choosePartition(topic);
        return fetch(topic, offset, partition);
    }

    @Override
    public SimpleFetchData fetch(String topic, Long offset, Integer partition) throws Exception {
        try {
            ILog log = GuiceDI.getInstance(LogManager.class).getOrCreateLog(topic, partition);
            logger.info(String.format("topic:%s offset:%s", topic, offset));
            MessageSet messageset = log.read(offset, 1);
            Iterator<MessageAndOffset> ite = messageset.iterator();
            List<SimpleFetchData> datas = Lists.newArrayList();
            while (ite.hasNext()) {
                MessageAndOffset one = ite.next();
                SimpleFetchData data = new SimpleFetchData();
                data.setTopic(topic);
                data.setPartition(partition);
                data.setOffset(one.offset);
                ByteBuffer payload = one.message.payload();
                byte[] tokenbyte = new byte[payload.limit()];
                payload.get(tokenbyte, 0, payload.limit());
                data.setData(new String(tokenbyte, Message.ENCODING));
                datas.add(data);
            }
            return CollectionUtil.isNotEmpty(datas) ? datas.get(0) : null;
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

}
