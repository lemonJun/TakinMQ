package com.takin.mq.producer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.takin.emmet.collection.CollectionUtil;
import com.takin.mq.log.ILog;
import com.takin.mq.log.LogManager;
import com.takin.mq.message.ByteBufferMessageSet;
import com.takin.mq.message.Message;
import com.takin.mq.message.StringProducerData;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl
public class ProducerServiceImpl implements ProducerService {

    private static final Logger logger = LoggerFactory.getLogger(ProducerServiceImpl.class);

    @Override
    public long send(StringProducerData data) throws Exception {
        try {
            logger.info(JSON.toJSONString(data));
            int partion = GuiceDI.getInstance(LogManager.class).choosePartition(data.getTopic());
            return send(data, partion);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public long send(StringProducerData data, int partition) throws Exception {
        try {
            ILog log = GuiceDI.getInstance(LogManager.class).getOrCreateLog(data.getTopic(), partition);
            byte[] databyte = data.getData().getBytes("utf-8");
            Message msg = new Message(databyte);
            ByteBufferMessageSet messageset = new ByteBufferMessageSet(msg);
            List<Long> address = log.append(messageset);
            logger.info(log.reallogfile());
            return CollectionUtil.isEmpty(address) ? 0 : address.get(0).longValue();
        } catch (Exception e) {
            throw e;
        }
    }

}
