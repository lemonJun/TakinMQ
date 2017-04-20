package io.jafka.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.anno.ServiceImpl;

import io.jafka.log.ILog;
import io.jafka.log.LogManager;

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
        logger.info(log.reallogfile());
        return 0;
    }

}
