package io.jafka.broker;

import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface ProducerService {

    public abstract void send(ProducerData data) throws Exception;

}
