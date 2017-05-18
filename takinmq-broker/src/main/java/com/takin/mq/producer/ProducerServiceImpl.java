package com.takin.mq.producer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.takin.mq.message.ByteBufferMessageSet;
import com.takin.mq.message.Message;
import com.takin.mq.message.SimpleSendData;
import com.takin.mq.store.ILog;
import com.takin.mq.store.LogManager;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl
public class ProducerServiceImpl implements ProducerService {

    private static final Logger logger = LoggerFactory.getLogger(ProducerServiceImpl.class);

    @Override
    public long send(SimpleSendData data) throws Exception {
        try {
            logger.info(JSON.toJSONString(data));
            int partion = GuiceDI.getInstance(LogManager.class).choosePartition(data.getTopic());
            return send(data, partion);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public long send(SimpleSendData data, Integer partition) throws Exception {
        try {
            ILog log = GuiceDI.getInstance(LogManager.class).getOrCreateLog(data.getTopic(), partition);
            byte[] databyte = data.getData().getBytes("utf-8");
            Message msg = new Message(databyte);
            ByteBufferMessageSet messageset = new ByteBufferMessageSet(msg);
            long offset = log.append(messageset);
            logger.info(log.reallogfile());
            return offset;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public long send(List<SimpleSendData> datas) throws Exception {
        return 0;
    }

    @Override
    public long send(List<SimpleSendData> datas, int partition) throws Exception {
        return 0;
    }

}
