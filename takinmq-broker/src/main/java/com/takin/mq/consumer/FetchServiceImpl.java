package com.takin.mq.consumer;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.mq.message.Message;
import com.takin.mq.message.MessageAndOffset;
import com.takin.mq.message.SimpleFetchData;
import com.takin.mq.store2.ILog;
import com.takin.mq.store2.LogManager;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.anno.ServiceImpl;

/**
 * 
 *
 * @author WangYazhou
 * @date  2017年5月22日 下午1:11:19
 * @see
 */
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
            MessageAndOffset messandoffset = log.read(offset, 1);
            SimpleFetchData data = new SimpleFetchData();
            data.setTopic(topic);
            data.setPartition(partition);
            data.setOffset(messandoffset.offset);
            ByteBuffer payload = messandoffset.message.payload();
            byte[] tokenbyte = new byte[payload.limit()];
            payload.get(tokenbyte, 0, payload.limit());
            data.setData(new String(tokenbyte, Message.ENCODING));

            //            
            //            Iterator<MessageAndOffset> ite = messageset.iterator();
            //            List<SimpleFetchData> datas = Lists.newArrayList();
            //            while (ite.hasNext()) {
            //                MessageAndOffset one = ite.next();
            //               
            //            }
            return data;
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

}
