package com.lemon.takinmq.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.common.BrokerConfig;
import com.lemon.takinmq.store.config.MessageStoreConfig;

public class MessageStoreFactory {

    private static final Logger logger = LoggerFactory.getLogger(MessageStoreFactory.class);

    private static final MessageStoreFactory instance = new MessageStoreFactory();

    public static MessageStoreFactory getInstance() {
        return instance;
    }

    private MessageStore messageStore;

    public MessageStore buildMessageStore(MessageStoreConfig messageStoreConfig, BrokerConfig brokerConfig) {
        this.messageStore = new DefaultMessageStore(messageStoreConfig, brokerConfig);//底层存储实现改成leveldb的话  
        return messageStore;
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }

}
