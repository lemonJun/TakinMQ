
package com.takin.mq.delay;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.takin.mq.broker.BrokerConfig;
import com.takin.mq.message.Message;
import com.takin.mq.message.SimpleSendData;
import com.takin.mq.store2.IStore;
import com.takin.mq.store2.LogManager;
import com.takin.rpc.server.GuiceDI;

/**
 * 实现一个任意延时时间的消息队列
 * 
 * 
 * 
 * @author WangYazhou
 * @date  2017年1月19日 下午12:32:26
 * @see
 */
@Singleton
public class DelayMessageService {

    private final String dbname = "delayTopic";

    //本打算一个主题对应一个DB  后来想了下   这个好像跟主题无关

    private BTreeMap<Long, SimpleSendData> btree;

    //用来于存储互通
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressWarnings("unchecked")
    @Inject
    private DelayMessageService() {
        String filepath = GuiceDI.getInstance(BrokerConfig.class).getLogdirs() + File.separator + dbname;
        DB db = DBMaker.fileDB(new File(filepath)).make();
        btree = (BTreeMap<Long, SimpleSendData>) db.treeMap("delay").keySerializer(Serializer.LONG)//
                        .counterEnable().createOrOpen();
        executor.submit(new DelayThread());
    }

    //写入一条数据
    public void put(long key, SimpleSendData value) {
        btree.put(key, value);
    }

    //后台读取消息线程
    final class DelayThread implements Runnable {
        @Override
        public void run() {
            //阻塞获取第一个值 
            try {
                Map.Entry<Long, SimpleSendData> entry = btree.pollFirstEntry();
                if (entry != null && entry.getValue() != null) {
                    SimpleSendData msg = entry.getValue();
                    int partion = GuiceDI.getInstance(LogManager.class).choosePartition(msg.getTopic());
                    IStore log = GuiceDI.getInstance(LogManager.class).getOrCreateLog(msg.getTopic(), partion);
                    Message messageg = new Message(msg.getData());
                    long offset = log.append(messageg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    final class TimeComparator implements Comparator<Long> {
        @Override
        public int compare(Long o1, Long o2) {
            //对日期字段进行升序，如果欲降序可采用before方法
            if (o1 > o2)
                return 1;
            return -1;
        }

    }

}
