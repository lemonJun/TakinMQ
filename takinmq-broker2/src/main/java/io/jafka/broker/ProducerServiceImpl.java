package io.jafka.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl
public class ProducerServiceImpl implements ProducerService {

    private static final Logger logger = LoggerFactory.getLogger(ProducerServiceImpl.class);

    @Override
    public int send(StringProducerData data) throws Exception {
        logger.info(JSON.toJSONString(data));
        return 1;
    }
    
    @Override
    public int send(StringProducerData data, int partition) throws Exception {
        return 0;
    }

}
