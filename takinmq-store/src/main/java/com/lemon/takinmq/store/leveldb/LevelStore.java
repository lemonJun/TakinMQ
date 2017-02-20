package com.lemon.takinmq.store.leveldb;

import java.io.File;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.lemon.takinmq.store.MessageExtBrokerInner;

public class LevelStore {

    private static final Logger logger = LoggerFactory.getLogger(LevelStore.class);
    private final Map<String, DB> dbMap = Maps.newConcurrentMap();
    private final ReentrantLock lock = new ReentrantLock();

    private DB makeSuceDB(String topic) {
        DB db = dbMap.get(topic);
        try {
            if (db == null) {
                lock.lock();
                Options options = new Options();
                options.createIfMissing(true);
                db = Iq80DBFactory.factory.open(new File(String.format("D:/takin/%s/", topic)), options);
                dbMap.put(topic, db);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return db;
    }

    //
    public void put(final MessageExtBrokerInner msg, String key) {
        DB db = makeSuceDB(msg.getTopic());
        db.put(key.getBytes(), msg.getBody());
    }

}
