package com.lemon.takinmq.broker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.broker.client.Broker2Client;
import com.lemon.takinmq.broker.client.ConsumerIdsChangeListener;
import com.lemon.takinmq.broker.client.ConsumerManager;
import com.lemon.takinmq.broker.client.DefaultConsumerIdsChangeListener;
import com.lemon.takinmq.broker.client.ProducerManager;
import com.lemon.takinmq.broker.latency.BrokerFastFailure;
import com.lemon.takinmq.broker.offset.ConsumerOffsetManager;
import com.lemon.takinmq.broker.polling.NotifyMessageArrivingListener;
import com.lemon.takinmq.broker.polling.PullRequestHoldService;
import com.lemon.takinmq.broker.process.PullMessageProcessor;
import com.lemon.takinmq.broker.slave.SlaveSynchronize;
import com.lemon.takinmq.broker.subscription.SubscriptionGroupManager;
import com.lemon.takinmq.broker.topic.TopicConfigManager;
import com.lemon.takinmq.common.BrokerConfig;
import com.lemon.takinmq.common.ImoduleService;
import com.lemon.takinmq.remoting.netty5.NettyClientConfig;
import com.lemon.takinmq.remoting.netty5.NettyServerConfig;
import com.lemon.takinmq.store.config.MessageStoreConfig;
import com.lemon.takinmq.store.stat.BrokerStatsManager;

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
    private final ConsumerIdsChangeListener consumerIdsChangeListener;
    private final ConsumerManager consumerManager;
    private final ProducerManager producerManager;
    //    private final ClientHousekeepingService ClientHousekeepingService  暂未用
    private final Broker2Client broker2Client;
    private final SubscriptionGroupManager subscriptionGroupManager;
    private final SlaveSynchronize slaveSynchronize;

    //阻塞队列
    private final BlockingQueue<Runnable> sendThreadPoolQueue;
    private final BlockingQueue<Runnable> pullThreadPoolQueue;
    private final BlockingQueue<Runnable> clientManagerThreadPoolQueue;
    //
    private final BrokerStatsManager brokerStatManager;
    private final BrokerFastFailure brokerFastFailure;

    //初始化对象
    public BrokerStartUp(BrokerConfig brokerConfig, NettyServerConfig nettyServerConfig, NettyClientConfig nettyClientConfig, MessageStoreConfig messageStoreConfig) {
        this.brokerConfig = new BrokerConfig();
        this.nettyServerConfig = nettyServerConfig;
        this.nettyClientConfig = nettyClientConfig;
        this.messageStoreConfig = messageStoreConfig;
        //创建主要服务对象
        this.consumerOffsetManager = new ConsumerOffsetManager(this);
        this.topicConfigManager = new TopicConfigManager(this);
        this.pullMessageProcessor = new PullMessageProcessor(this);
        this.pullRequestHoldService = new PullRequestHoldService(this);
        this.notifyMessageArrivingListener = new NotifyMessageArrivingListener(pullRequestHoldService);
        this.consumerIdsChangeListener = new DefaultConsumerIdsChangeListener(this);
        this.consumerManager = new ConsumerManager(consumerIdsChangeListener);
        this.producerManager = new ProducerManager();
        this.broker2Client = new Broker2Client(this);
        this.subscriptionGroupManager = new SubscriptionGroupManager(this);
        this.slaveSynchronize = new SlaveSynchronize(this);

        this.sendThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.brokerConfig.getSendThreadPoolQueueCapacity());

        this.pullThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.brokerConfig.getPullThreadPoolQueueCapacity());
        this.clientManagerThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.brokerConfig.getClientManagerThreadPoolQueueCapacity());
        //        this.consumerManagerThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.brokerConfig.getConsumerManagerThreadPoolQueueCapacity());

        this.brokerStatManager = new BrokerStatsManager(this.brokerConfig.getBrokerClusterName());
        this.brokerFastFailure = new BrokerFastFailure(this);
    }

    @Override
    public void init() throws Exception {
        boolean result = true;
        loadconfig();//读取配置文件
        if (result) {
            //创建消息持久化服务
        }
        
        
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void loadconfig() {

    }

    public BrokerConfig getBrokerConfig() {
        return brokerConfig;
    }

    public NettyServerConfig getNettyServerConfig() {
        return nettyServerConfig;
    }

    public NettyClientConfig getNettyClientConfig() {
        return nettyClientConfig;
    }

}
