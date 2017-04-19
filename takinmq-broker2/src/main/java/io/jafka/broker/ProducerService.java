package io.jafka.broker;

import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface ProducerService {

    public abstract int send(StringProducerData data) throws Exception;

}
