package com.lemon.takinmq.broker;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.lemon.takinmq.common.datainfo.PutMessageResult;
import com.lemon.takinmq.common.datainfo.PutMessageStatus;
import com.lemon.takinmq.common.datainfo.SendMessageRequestHeader;
import com.lemon.takinmq.common.message.Message;
import com.lemon.takinmq.common.message.MessageAccessor;
import com.lemon.takinmq.common.message.MessageDecoder;
import com.lemon.takinmq.common.message.PullMessageRequestHeader;
import com.lemon.takinmq.common.message.PullResult;
import com.lemon.takinmq.common.service.IBrokerService;
import com.lemon.takinmq.store.GetMessageResult;
import com.lemon.takinmq.store.MessageExtBrokerInner;
import com.lemon.takinmq.store.MessageStoreFactory;

/**
 * 
 *
 * @author WangYazhou
 * @date  2017年2月18日 下午6:36:56
 * @see
 */
public class BrokerServiceImpl implements IBrokerService {

    private static final Logger logger = LoggerFactory.getLogger(BrokerServiceImpl.class);

    public BrokerServiceImpl() {
        logger.info("brokerservice init succ");
    }

    @Override
    public PutMessageResult sendMessage(Message message, SendMessageRequestHeader requestHeader) throws Exception {
        try {
            logger.info("receive" + JSON.toJSONString(message));
            //组装一个消息实体
            MessageExtBrokerInner msgInner = new MessageExtBrokerInner();
            msgInner.setTopic(message.getTopic());
            msgInner.setBody(message.getBody());
            msgInner.setFlag(requestHeader.getFlag());
            MessageAccessor.setProperties(msgInner, MessageDecoder.string2messageProperties(requestHeader.getProperties()));
            msgInner.setPropertiesString(requestHeader.getProperties());
            //            msgInner.setTagsCode(MessageExtBrokerInner.tagsString2tagsCode(topicConfig.getTopicFilterType(), msgInner.getTags()));

            msgInner.setQueueId(requestHeader.getQueueId());
            msgInner.setSysFlag(requestHeader.getSysFlag());
            msgInner.setBornTimestamp(requestHeader.getBornTimestamp());
            //            msgInner.setBornHost();
            msgInner.setStoreHost(this.getStoreHost());
            msgInner.setReconsumeTimes(requestHeader.getReconsumeTimes());

            PutMessageResult putMessageResult = MessageStoreFactory.getInstance().getMessageStore().putMessage(msgInner);
            return putMessageResult;
        } catch (Exception e) {
            logger.error("put message error", e);
        }
        return new PutMessageResult(PutMessageStatus.UNKNOWN_ERROR);

    }

    public SocketAddress getStoreHost() {
        return null;
    }

    @Override
    public void consumerSendMsgBack() throws Exception {

    }

    @Override
    public void queryMessage(String msgid) throws Exception {

    }

    @Override
    public PullResult pullMessage(PullMessageRequestHeader pullRequest) throws Exception {
        PullResult result = new PullResult();
        PullResult msgresult = MessageStoreFactory.getInstance().getMessageStore().getMessage(pullRequest.getConsumerGroup(), //
                        pullRequest.getTopic(), pullRequest.getQueueId(), pullRequest.getQueueOffset(), //
                        pullRequest.getMaxMsgNums(), pullRequest.getSubscription());

        return result;
    }

    @Override
    public void getConsumerListByGroup(String group) throws Exception {

    }

    @Override
    public void queryConsumerOffset(String topic, String client) throws Exception {

    }

    @Override
    public void registerClient(String topic, String client) throws Exception {

    }

    @Override
    public void unregisterClient(String topic, String client) throws Exception {

    }

    @Override
    public void createTopic(String topic) throws Exception {

    }

    @Override
    public void getAllTopicConfig(String topic) throws Exception {

    }

}
