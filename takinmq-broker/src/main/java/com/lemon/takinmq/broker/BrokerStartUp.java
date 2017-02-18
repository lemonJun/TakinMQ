package com.lemon.takinmq.broker;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.broker.client.Broker2Client;
import com.lemon.takinmq.broker.client.BrokerOuterAPI;
import com.lemon.takinmq.broker.client.ConsumerIdsChangeListener;
import com.lemon.takinmq.broker.client.ConsumerManager;
import com.lemon.takinmq.broker.client.DefaultConsumerIdsChangeListener;
import com.lemon.takinmq.broker.client.ProducerManager;
import com.lemon.takinmq.broker.latency.BrokerFastFailure;
import com.lemon.takinmq.broker.latency.BrokerFixedThreadPoolExecutor;
import com.lemon.takinmq.broker.offset.ConsumerOffsetManager;
import com.lemon.takinmq.broker.polling.NotifyMessageArrivingListener;
import com.lemon.takinmq.broker.polling.PullRequestHoldService;
import com.lemon.takinmq.broker.process.PullMessageProcessor;
import com.lemon.takinmq.broker.slave.SlaveSynchronize;
import com.lemon.takinmq.broker.subscription.SubscriptionGroupManager;
import com.lemon.takinmq.broker.topic.TopicConfigManager;
import com.lemon.takinmq.common.BrokerConfig;
import com.lemon.takinmq.common.ImoduleService;
import com.lemon.takinmq.common.PermName;
import com.lemon.takinmq.common.ThreadFactoryImpl;
import com.lemon.takinmq.common.TopicConfig;
import com.lemon.takinmq.common.datainfo.TopicConfigSerializeWrapper;
import com.lemon.takinmq.common.naming.RegisterBrokerResult;
import com.lemon.takinmq.common.stat.MomentStatsItem;
import com.lemon.takinmq.common.util.UtilAll;
import com.lemon.takinmq.remoting.netty5.NettyClientConfig;
import com.lemon.takinmq.remoting.netty5.NettyServerConfig;
import com.lemon.takinmq.remoting.netty5.RemotingNettyServer;
import com.lemon.takinmq.store.DefaultMessageStore;
import com.lemon.takinmq.store.MessageStore;
import com.lemon.takinmq.store.config.BrokerRole;
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

    //
    private MessageStore messageStore;
    private RemotingNettyServer remotingServer;
    private final BrokerOuterAPI brokerOuterAPI;

    //线程池
    private ExecutorService sendMessageExecutor;
    private ExecutorService pullMessageExecutor;
    private ExecutorService adminBrokerExecutor;
    private ExecutorService clientManageExecutor;
    private ExecutorService consumerManageExecutor;

    //定时任务
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("BrokerControllerScheduledThread"));
    //
    private boolean updateMasterHAServerAddrPeriodically = false;

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
        this.brokerOuterAPI = new BrokerOuterAPI(nettyClientConfig);
        logger.info("init constructor");
    }

    @Override
    public void init() throws Exception {
        boolean result = true;
        loadconfig();//读取配置文件

        //设置默认的naming地址
        if (this.brokerConfig.getNamesrvAddr() != null) {
            this.brokerOuterAPI.updateNameServerAddressList(this.brokerConfig.getNamesrvAddr());
            logger.info("user specfied name server address: {}", this.brokerConfig.getNamesrvAddr());
        }
        if (result) {
            //创建消息持久化服务
            this.messageStore = new DefaultMessageStore(messageStoreConfig, brokerConfig);//底层存储实现改成leveldb的话  
        }
        result = result & this.messageStore.load();//重启时 加载数据
        if (result) {
            this.remotingServer = new RemotingNettyServer(this.nettyServerConfig);
        }

        this.sendMessageExecutor = new BrokerFixedThreadPoolExecutor(brokerConfig.getSendMessageThreadPoolNums(), this.brokerConfig.getSendMessageThreadPoolNums(), 1000 * 60, TimeUnit.MILLISECONDS, this.sendThreadPoolQueue, new ThreadFactoryImpl("SendMessageThread_"));
        this.pullMessageExecutor = new BrokerFixedThreadPoolExecutor(brokerConfig.getPullMessageThreadPoolNums(), this.brokerConfig.getPullMessageThreadPoolNums(), 1000 * 60, TimeUnit.MILLISECONDS, this.pullThreadPoolQueue, new ThreadFactoryImpl("PullMessageThread_"));
        this.adminBrokerExecutor = Executors.newFixedThreadPool(brokerConfig.getAdminBrokerThreadPoolNums(), new ThreadFactoryImpl("AdminBrokerThread_"));
        this.clientManageExecutor = new ThreadPoolExecutor(brokerConfig.getClientManageThreadPoolNums(), this.brokerConfig.getClientManageThreadPoolNums(), 1000 * 60, TimeUnit.MILLISECONDS, this.clientManagerThreadPoolQueue, new ThreadFactoryImpl("ClientManageThread_"));
        this.consumerManageExecutor = Executors.newFixedThreadPool(brokerConfig.getConsumerManageThreadPoolNums(), new ThreadFactoryImpl("ConsumerManageThread_"));

        registerProcessor();
        //        scheduler();
        //

    }

    @Override
    public void start() throws Exception {
        this.brokerOuterAPI.start();
        this.registerBrokerAll(false, false);
    }

    private void scheduler() {
        final long initialDelay = UtilAll.computNextMorningTimeMillis() - System.currentTimeMillis();
        final long period = 1000 * 60 * 60 * 24;
        //        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
        //            @Override
        //            public void run() {
        //                try {
        //                    BrokerStartUp.this.getBrokerStats().record();
        //                } catch (Throwable e) {
        //                    logger.error("schedule record error.", e);
        //                }
        //            }
        //        }, initialDelay, period, TimeUnit.MILLISECONDS);

        //周期记录消费进度
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    BrokerStartUp.this.consumerOffsetManager.persist();
                } catch (Throwable e) {
                    logger.error("schedule persist consumerOffset error.", e);
                }
            }
        }, 1000 * 10, this.brokerConfig.getFlushConsumerOffsetInterval(), TimeUnit.MILLISECONDS);

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    BrokerStartUp.this.protectBroker();
                } catch (Exception e) {
                    logger.error("protectBroker error.", e);
                }
            }
        }, 3, 3, TimeUnit.MINUTES);

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    BrokerStartUp.this.printWaterMark();
                } catch (Exception e) {
                    logger.error("printWaterMark error.", e);
                }
            }
        }, 10, 1, TimeUnit.SECONDS);

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    logger.info("dispatch behind commit log {} bytes", BrokerStartUp.this.getMessageStore().dispatchBehindBytes());
                } catch (Throwable e) {
                    logger.error("schedule dispatchBehindBytes error.", e);
                }
            }
        }, 1000 * 10, 1000 * 60, TimeUnit.MILLISECONDS);

        //naming改到另一个地方统一维护
        //
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    BrokerStartUp.this.brokerOuterAPI.fetchNameServerAddr();
                } catch (Throwable e) {
                    logger.error("ScheduledTask fetchNameServerAddr exception", e);
                }
            }
        }, 1000 * 10, 1000 * 60 * 2, TimeUnit.MILLISECONDS);

        //如果是副本  则启动定时同步
        if (BrokerRole.SLAVE == this.messageStoreConfig.getBrokerRole()) {
            if (this.messageStoreConfig.getHaMasterAddress() != null && this.messageStoreConfig.getHaMasterAddress().length() >= 6) {
                this.messageStore.updateHaMasterAddress(this.messageStoreConfig.getHaMasterAddress());
                this.updateMasterHAServerAddrPeriodically = false;
            } else {
                this.updateMasterHAServerAddrPeriodically = true;
            }

            this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    try {
                        BrokerStartUp.this.slaveSynchronize.syncAll();
                    } catch (Throwable e) {
                        logger.error("ScheduledTask syncAll slave exception", e);
                    }
                }
            }, 1000 * 10, 1000 * 60, TimeUnit.MILLISECONDS);
        } else {
            this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        BrokerStartUp.this.printMasterAndSlaveDiff();
                    } catch (Throwable e) {
                        logger.error("schedule printMasterAndSlaveDiff error.", e);
                    }
                }
            }, 1000 * 10, 1000 * 60, TimeUnit.MILLISECONDS);
        }

        //周期注册
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    BrokerStartUp.this.registerBrokerAll(true, false);
                } catch (Throwable e) {
                    logger.error("registerBrokerAll Exception", e);
                }
            }
        }, 1000 * 10, 1000 * 30, TimeUnit.MILLISECONDS);
    }

    /**
     * N多地方都需要注册
     * 向naming注册broker
     * @param checkOrderConfig
     * @param oneway
     */
    public synchronized void registerBrokerAll(final boolean checkOrderConfig, boolean oneway) {
        TopicConfigSerializeWrapper topicConfigWrapper = this.topicConfigManager.buildTopicConfigSerializeWrapper();

        // 
        if (!PermName.isWriteable(this.getBrokerConfig().getBrokerPermission()) || !PermName.isReadable(this.getBrokerConfig().getBrokerPermission())) {
            ConcurrentHashMap<String, TopicConfig> topicConfigTable = new ConcurrentHashMap<String, TopicConfig>();
            for (TopicConfig topicConfig : topicConfigWrapper.getTopicConfigTable().values()) {
                TopicConfig tmp = new TopicConfig(topicConfig.getTopicName(), topicConfig.getReadQueueNums(), topicConfig.getWriteQueueNums(), this.brokerConfig.getBrokerPermission());
                topicConfigTable.put(topicConfig.getTopicName(), tmp);
            }
            topicConfigWrapper.setTopicConfigTable(topicConfigTable);
        }
        logger.info(String.format("[Regist] %s %s %s %s %s %s %s %s %s", this.brokerConfig.getBrokerClusterName(), this.getBrokerAddr(), this.brokerConfig.getBrokerName(), this.brokerConfig.getBrokerId(), this.getHAServerAddr(), topicConfigWrapper, null, oneway, this.brokerConfig.getRegisterBrokerTimeoutMills()));
        RegisterBrokerResult registerBrokerResult = this.brokerOuterAPI.registerBrokerAll(this.brokerConfig.getBrokerClusterName(), this.getBrokerAddr(), this.brokerConfig.getBrokerName(), this.brokerConfig.getBrokerId(), this.getHAServerAddr(), topicConfigWrapper, null, oneway, this.brokerConfig.getRegisterBrokerTimeoutMills());

        if (registerBrokerResult != null) {
            if (this.updateMasterHAServerAddrPeriodically && registerBrokerResult.getHaServerAddr() != null) {
                this.messageStore.updateHaMasterAddress(registerBrokerResult.getHaServerAddr());
            }

            this.slaveSynchronize.setMasterAddr(registerBrokerResult.getMasterAddr());

            if (checkOrderConfig) {
                this.getTopicConfigManager().updateOrderTopicConfig(registerBrokerResult.getKvTable());
            }
        }
    }

    public String getBrokerAddr() {
        return this.brokerConfig.getBrokerIP1() + ":" + this.nettyServerConfig.getListenPort();
    }

    public TopicConfigManager getTopicConfigManager() {
        return topicConfigManager;
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }

    private String getHAServerAddr() {
        return "";
    }

    //打
    public void printWaterMark() {
        //        logger.info("[WATERMARK] Send Queue Size: {} SlowTimeMills: {}", this.sendThreadPoolQueue.size(), headSlowTimeMills4SendThreadPoolQueue());
        //        logger.info("[WATERMARK] Pull Queue Size: {} SlowTimeMills: {}", this.pullThreadPoolQueue.size(), headSlowTimeMills4PullThreadPoolQueue());
    }

    //保护broker
    public void protectBroker() {
        if (this.brokerConfig.isDisableConsumeIfConsumerReadSlowly()) {
            final Iterator<Map.Entry<String, MomentStatsItem>> it = this.brokerStatManager.getMomentStatsItemSetFallSize().getStatsItemTable().entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<String, MomentStatsItem> next = it.next();
                final long fallBehindBytes = next.getValue().getValue().get();
                if (fallBehindBytes > this.brokerConfig.getConsumerFallbehindThreshold()) {
                    final String[] split = next.getValue().getStatsKey().split("@");
                    final String group = split[2];
                    logger.info("[PROTECT_BROKER] the consumer[{}] consume slowly, {} bytes, disable it", group, fallBehindBytes);
                    this.subscriptionGroupManager.disableConsume(group);
                }
            }
        }
    }

    private void printMasterAndSlaveDiff() {
        long diff = this.messageStore.slaveFallBehindMuch();
        // XXX: warn and notify me
        logger.info("slave fall behind master, how much, {} bytes", diff);
    }

    /**
     * 注册broker特有的nettyhandler类
     * 虽然没用到  但可以先留着
     */
    private void registerProcessor() {

    }

    @Override
    public void shutdown() throws Exception {
        this.scheduledExecutorService.shutdown();
        try {
            this.scheduledExecutorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
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
