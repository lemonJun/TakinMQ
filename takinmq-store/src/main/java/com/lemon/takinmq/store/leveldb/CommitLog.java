package com.lemon.takinmq.store.leveldb;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FileUtils;
import org.iq80.leveldb.impl.FileChannelLogWriter;
import org.iq80.leveldb.impl.LogMonitor;
import org.iq80.leveldb.impl.LogReader;
import org.iq80.leveldb.impl.LogWriter;
import org.iq80.leveldb.util.Slice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.lemon.takinmq.common.datainfo.PutMessageResult;
import com.lemon.takinmq.common.datainfo.PutMessageStatus;
import com.lemon.takinmq.common.message.MessageExt;
import com.lemon.takinmq.common.message.PullResult;
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
    private final Map<String, LogWriter> writerMap = Maps.newConcurrentMap();
    private final Map<String, LogReader> readerMap = Maps.newConcurrentMap();
    private final ReentrantLock lock = new ReentrantLock();

    private final LevelStore levelStore;

    public CommitLog(final DefaultMessageStore defaultMessageStore) {
        this.defaultMessageStore = defaultMessageStore;
        this.levelStore = new LevelStore();
    }

    private LogWriter makeSureTopic(String topic) {
        LogWriter writer = writerMap.get(topic);
        try {
            if (writer == null) {
                lock.lock();
                String path = String.format("D:/takin/%s", topic);
                FileUtils.forceMkdir(new File(path));
                writer = new FileChannelLogWriter(new File(String.format("%s/queue", path, topic)), 1, false);
                writerMap.put(topic, writer);
                LogReader reader = new LogReader(new FileInputStream(new File(path)).getChannel(), throwExceptionMonitor(), true, 0);
                readerMap.put(topic, reader);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("make file error", e);
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return writer;
    }

    public PutMessageResult putMessage(final MessageExtBrokerInner msg) throws Exception {
        PutMessageResult result = new PutMessageResult(PutMessageStatus.PUT_OK);
        LogWriter writer = makeSureTopic(msg.getTopic());
        String key = String.valueOf(SnowFlakeUuid.getInstance().nextId());
        Slice record = new Slice(key.getBytes());//写入队列
        writer.addRecord(record, true);//写入消息数据
        levelStore.put(msg, key);
        return result;
    }

    public PullResult readMessage(String topic) {
        LogReader reader = readerMap.get(topic);
        Slice record = reader.readRecord();
        MessageExt msg = new MessageExt();
        msg.setTopic(topic);
        msg.setBody(record.getBytes());
        PullResult result = new PullResult();
        result.getMsgFoundList().add(msg);
        return result;
    }

    public static LogMonitor throwExceptionMonitor() {
        return new LogMonitor() {
            @Override
            public void corruption(long bytes, String reason) {
                throw new RuntimeException(String.format("corruption of %s bytes: %s", bytes, reason));
            }

            @Override
            public void corruption(long bytes, Throwable reason) {
                throw new RuntimeException(String.format("corruption of %s bytes", bytes), reason);
            }
        };
    }

}
