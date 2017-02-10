package com.lemon.takinmq.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.broker.offset.ConsumerOffsetManager;
import com.lemon.takinmq.broker.polling.NotifyMessageArrivingListener;
import com.lemon.takinmq.broker.polling.PullRequestHoldService;
import com.lemon.takinmq.broker.process.PullMessageProcessor;
import com.lemon.takinmq.broker.topic.TopicConfigManager;
import com.lemon.takinmq.common.BrokerConfig;
import com.lemon.takinmq.common.ImoduleService;
import com.lemon.takinmq.remoting.netty5.NettyClientConfig;
import com.lemon.takinmq.remoting.netty5.NettyServerConfig;
import com.lemon.takinmq.store.config.MessageStoreConfig;

/**
 * broker启动类
 *
 * @author WangYazhou
 * @date  2017年2月9日 下午5:41:18
 * @see
 */
public class BrokerStartUp implements ImoduleService {

    private static final Logger logger = LoggerFactory.getLogger(BrokerStartUp.class);
    private final BrokerConfig brokerConfig;
    private final NettyServerConfig nettyServerConfig;
    private final NettyClientConfig nettyClientConfig;
    private final MessageStoreConfig messageStoreConfig;
    private final ConsumerOffsetManager consumerOffsetManager;
    private final TopicConfigManager topicConfigManager;
    private final PullMessageProcessor pullMessageProcessor;
    private final PullRequestHoldService pullRequestHoldService;
    private final NotifyMessageArrivingListener notifyMessageArrivingListener;

    //初始化对象
    public BrokerStartUp(BrokerConfig brokerConfig, NettyServerConfig nettyServerConfig, NettyClientConfig nettyClientConfig, MessageStoreConfig messageStoreConfig) {
        this.brokerConfig = new BrokerConfig();
        this.nettyServerConfig = nettyServerConfig;
        this.nettyClientConfig = nettyClientConfig;
        this.messageStoreConfig = messageStoreConfig;
        loadconfig();//读取配置文件
        //创建主要服务对象
        this.consumerOffsetManager = new ConsumerOffsetManager(this);
        this.topicConfigManager = new TopicConfigManager(this);
        this.pullMessageProcessor = new PullMessageProcessor(this);
        this.pullRequestHoldService = new PullRequestHoldService(this);
        this.notifyMessageArrivingListener = new NotifyMessageArrivingListener(pullRequestHoldService);
        
    }

    @Override
    public void loadconfig() {

    }

    @Override
    public void init() throws Exception {
        //初始化配置

    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }

}
