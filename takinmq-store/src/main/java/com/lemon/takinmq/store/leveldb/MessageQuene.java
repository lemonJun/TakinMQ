package com.lemon.takinmq.store.leveldb;

import java.io.File;
import java.io.FileNotFoundException;

import org.iq80.leveldb.impl.FileChannelLogWriter;
import org.iq80.leveldb.impl.LogWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.common.datainfo.PutMessageResult;
import com.lemon.takinmq.common.datainfo.PutMessageStatus;
import com.lemon.takinmq.store.DefaultMessageStore;
import com.lemon.takinmq.store.MessageExtBrokerInner;

public class MessageQuene {

    private static final Logger logger = LoggerFactory.getLogger(MessageQuene.class);

    private final DefaultMessageStore defaultMessageStore;
    private LogWriter msgWriter;

    public MessageQuene(final DefaultMessageStore defaultMessageStore) {
        this.defaultMessageStore = defaultMessageStore;
        this.makeSureFile();
    }

    private void makeSureFile() {
        try {
            msgWriter = new FileChannelLogWriter(new File("D:/sfile/slice2"), 1, false);
        } catch (FileNotFoundException e) {
            logger.error("make file error", e);
        }
    }

    private String makeFileName() {
        return "queue";
    }

    public PutMessageResult putMessage(final MessageExtBrokerInner msg) {
        PutMessageResult result = new PutMessageResult(PutMessageStatus.PUT_OK);

        return result;
    }

}
