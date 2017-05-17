package com.takin.mq.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
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
    public int send(StringProducerData data) throws Exception {
        logger.info(JSON.toJSONString(data));
        int partion = GuiceDI.getInstance(LogManager.class).choosePartition(data.getTopic());
        return send(data, partion);
    }

    @Override
    public int send(StringProducerData data, int partition) throws Exception {
        ILog log = GuiceDI.getInstance(LogManager.class).getOrCreateLog(data.getTopic(), partition);
        byte[] databyte = data.getData().getBytes("utf-8");
        Message msg = new Message(databyte);
        ByteBufferMessageSet messageset = new ByteBufferMessageSet(msg);
        log.append(messageset);
        logger.info(log.reallogfile());
        return 0;
    }

}
