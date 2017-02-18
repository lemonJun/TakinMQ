package com.lemon.takinmq.broker;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.common.datainfo.PutMessageResult;
import com.lemon.takinmq.common.datainfo.PutMessageStatus;
import com.lemon.takinmq.common.datainfo.SendMessageRequestHeader;
import com.lemon.takinmq.common.datainfo.TopicConfigSerializeWrapper;
import com.lemon.takinmq.common.message.MessageAccessor;
import com.lemon.takinmq.common.message.MessageDecoder;
import com.lemon.takinmq.common.naming.RegisterBrokerResult;
import com.lemon.takinmq.common.service.IBrokerService;
import com.lemon.takinmq.store.MessageExtBrokerInner;

public class BrokerServiceImpl implements IBrokerService {

    private static final Logger logger = LoggerFactory.getLogger(BrokerServiceImpl.class);

    private final BrokerStartUp brokerStartup;
    protected final SocketAddress storeHost;

    public BrokerServiceImpl(BrokerStartUp brokerStartup) {
        this.brokerStartup = brokerStartup;
        this.storeHost = new InetSocketAddress(brokerStartup.getBrokerConfig().getBrokerIP1(), brokerStartup.getNettyServerConfig().getListenPort());
    }

    @Override
    public RegisterBrokerResult register(String clustername, String brokeraddress, String brokername, Long brokerId, TopicConfigSerializeWrapper topic) throws Exception {
        return null;
    }

    @Override
    public PutMessageResult sendMessage(String message, SendMessageRequestHeader requestHeader) throws Exception {
        try {
            //组装一个消息实体
            MessageExtBrokerInner msgInner = new MessageExtBrokerInner();
            msgInner.setTopic(requestHeader.getTopic());
            msgInner.setBody(message.getBytes());
            msgInner.setFlag(requestHeader.getFlag());
            MessageAccessor.setProperties(msgInner, MessageDecoder.string2messageProperties(requestHeader.getProperties()));
            msgInner.setPropertiesString(requestHeader.getProperties());
            //            msgInner.setTagsCode(MessageExtBrokerInner.tagsString2tagsCode(topicConfig.getTopicFilterType(), msgInner.getTags()));

            msgInner.setQueueId(requestHeader.getQueueId());
            msgInner.setSysFlag(requestHeader.getSysFlag());
            msgInner.setBornTimestamp(requestHeader.getBornTimestamp());
            //            msgInner.setBornHost();
            msgInner.setStoreHost(this.getStoreHost());
            msgInner.setReconsumeTimes(requestHeader.getReconsumeTimes() == null ? 0 : requestHeader.getReconsumeTimes());

            PutMessageResult putMessageResult = this.brokerStartup.getMessageStore().putMessage(msgInner);
            return putMessageResult;
        } catch (Exception e) {
            logger.error("put message error", e);
        }
        return new PutMessageResult(PutMessageStatus.UNKNOWN_ERROR);

    }

    public SocketAddress getStoreHost() {
        return storeHost;
    }

}
