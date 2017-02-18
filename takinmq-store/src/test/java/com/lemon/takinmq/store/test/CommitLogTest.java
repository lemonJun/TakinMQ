package com.lemon.takinmq.store.test;

import org.apache.log4j.PropertyConfigurator;

import com.lemon.takinmq.store.MessageExtBrokerInner;
import com.lemon.takinmq.store.leveldb.CommitLog;

public class CommitLogTest {

    public static void main(String[] args) {
        PropertyConfigurator.configure("D:/log4j.properties");

        put();
    }

    private static void put() {
        try {
            CommitLog commit = new CommitLog(null);
            for (int i = 0; i < 10; i++) {
                MessageExtBrokerInner msg = new MessageExtBrokerInner();
                msg.setTopic("test");
                msg.setBody(String.format("test%s", i).getBytes());
                commit.putMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
