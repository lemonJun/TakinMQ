package com.lemon.takinmq.broker;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.lemon.takinmq.common.datainfo.SendMessageRequestHeader;
import com.lemon.takinmq.common.datainfo.TopicConfigSerializeWrapper;
import com.lemon.takinmq.common.message.MessageAccessor;
import com.lemon.takinmq.common.message.MessageDecoder;
import com.lemon.takinmq.common.naming.RegisterBrokerResult;
import com.lemon.takinmq.common.service.IBrokerService;
import com.lemon.takinmq.store.MessageExtBrokerInner;
import com.lemon.takinmq.store.PutMessageResult;

public class BrokerServiceImpl implements IBrokerService {

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
    public RegisterBrokerResult sendMessage(String message, SendMessageRequestHeader requestHeader) throws Exception {
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

        } catch (Exception e) {
        }
        return null;
    }

    public SocketAddress getStoreHost() {
        return storeHost;
    }

}
