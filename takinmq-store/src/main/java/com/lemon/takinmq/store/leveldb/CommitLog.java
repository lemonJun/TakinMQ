package com.lemon.takinmq.store.leveldb;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.iq80.leveldb.impl.FileChannelLogWriter;
import org.iq80.leveldb.impl.LogWriter;
import org.iq80.leveldb.util.Slice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.lemon.takinmq.common.datainfo.PutMessageResult;
import com.lemon.takinmq.common.datainfo.PutMessageStatus;
import com.lemon.takinmq.common.util.SnowFlakeUuid;
import com.lemon.takinmq.store.DefaultMessageStore;
import com.lemon.takinmq.store.MessageExtBrokerInner;

/**
 * 在文件中存储所有的消息队列
 * 真正的消息数据存储在LevelStore中
 * 
 * @author WangYazhou
 * @date  2017年2月18日 下午12:04:21
 * @see
 */
public class CommitLog {

    private static final Logger logger = LoggerFactory.getLogger(CommitLog.class);

    private final DefaultMessageStore defaultMessageStore;
    private final Map<String, LogWriter> topicMap = Maps.newConcurrentMap();
    private final ReentrantLock lock = new ReentrantLock();

    private final LevelStore levelStore;

    public CommitLog(final DefaultMessageStore defaultMessageStore) {
        this.defaultMessageStore = defaultMessageStore;
        this.levelStore = new LevelStore();
    }

    private LogWriter makeSureFile(String topic) {
        LogWriter writer = topicMap.get(topic);
        try {
            if (writer == null) {
                lock.lock();
                writer = new FileChannelLogWriter(new File(String.format("D:/takin/%s/queue", topic)), 1, false);
                topicMap.put(topic, writer);
            }
        } catch (FileNotFoundException e) {
            logger.error("make file error", e);
        } finally {
            lock.unlock();
        }
        return writer;
    }

    public PutMessageResult putMessage(final MessageExtBrokerInner msg) throws Exception {
        PutMessageResult result = new PutMessageResult(PutMessageStatus.PUT_OK);
        LogWriter writer = makeSureFile(msg.getTopic());
        String key = String.valueOf(SnowFlakeUuid.getInstance().nextId());
        Slice record = new Slice(key.getBytes());//写入队列
        writer.addRecord(record, true);//写入消息数据
        levelStore.put(msg, key);
        return result;
    }
    
    
    

}
