package com.lemon.takinmq.store;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import com.lemon.takinmq.common.BrokerConfig;
import com.lemon.takinmq.common.ThreadFactoryImpl;
import com.lemon.takinmq.common.heartbeat.SubscriptionData;
import com.lemon.takinmq.common.message.MessageExt;
import com.lemon.takinmq.common.util.SystemClock;
import com.lemon.takinmq.store.config.MessageStoreConfig;
import com.lemon.takinmq.store.delay.ScheduleMessageService;
import com.lemon.takinmq.store.index.IndexService;

public class DefaultMessageStore implements MessageStore {

    private final MessageStoreConfig messageStoreConfig;

    private final CommitLog commitlog;

    //存入的是每一个主题 下的队列ID与消费进度情况
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, ConsumeQueue>> consumeMap = new ConcurrentHashMap<>();
    private final IndexService indexService;
    private final StoreStatsService storeStatService;
    private final ScheduleMessageService scheduleMessageService;

    private final RunningFlags runningFlags = new RunningFlags();
    private final SystemClock systemClock = SystemClock.instance();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("StoreScheduledThread"));

    private final BrokerConfig brokerConfig;

    private volatile boolean shutdown = true;
    private AtomicLong printTimes = new AtomicLong(0);

    public DefaultMessageStore(final MessageStoreConfig messageStoreConfig, final BrokerConfig brokerConfig) {
        this.messageStoreConfig = messageStoreConfig;
        this.storeStatService = new StoreStatsService();
        this.commitlog = new CommitLog(this);
        this.indexService = new IndexService();
        this.scheduleMessageService = new ScheduleMessageService();
        this.brokerConfig = brokerConfig;
    }

    public StoreStatsService getStoreStatService() {
        return storeStatService;
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public PutMessageResult putMessage(MessageExtBrokerInner msg) {
        return null;
    }

    @Override
    public GetMessageResult getMessage(String group, String topic, int queueId, long offset, int maxMsgNums, SubscriptionData subscriptionData) {
        return null;
    }

    @Override
    public long getMaxOffsetInQuque(String topic, int queueId) {
        return 0;
    }

    @Override
    public long getMinOffsetInQuque(String topic, int queueId) {
        return 0;
    }

    @Override
    public long getCommitLogOffsetInQueue(String topic, int queueId, long cqOffset) {
        return 0;
    }

    @Override
    public long getOffsetInQueueByTime(String topic, int queueId, long timestamp) {
        return 0;
    }

    @Override
    public MessageExt lookMessageByOffset(long commitLogOffset) {
        return null;
    }

    @Override
    public SelectMappedBufferResult selectOneMessageByOffset(long commitLogOffset) {
        return null;
    }

    @Override
    public SelectMappedBufferResult selectOneMessageByOffset(long commitLogOffset, int msgSize) {
        return null;
    }

    @Override
    public String getRunningDataInfo() {
        return null;
    }

    @Override
    public HashMap<String, String> getRuntimeInfo() {
        return null;
    }

    @Override
    public long getMaxPhyOffset() {
        return 0;
    }

    @Override
    public long getMinPhyOffset() {
        return 0;
    }

    @Override
    public long getEarliestMessageTime(String topic, int queueId) {
        return 0;
    }

    @Override
    public long getEarliestMessageTime() {
        return 0;
    }

    @Override
    public long getMessageStoreTimeStamp(String topic, int queueId, long offset) {
        return 0;
    }

    @Override
    public long getMessageTotalInQueue(String topic, int queueId) {
        return 0;
    }

    @Override
    public SelectMappedBufferResult getCommitLogData(long offset) {
        return null;
    }

    @Override
    public boolean appendToCommitLog(long startOffset, byte[] data) {
        return false;
    }

    @Override
    public void excuteDeleteFilesManualy() {

    }

    @Override
    public QueryMessageResult queryMessage(String topic, String key, int maxNum, long begin, long end) {
        return null;
    }

    @Override
    public void updateHaMasterAddress(String newAddr) {

    }

    @Override
    public long slaveFallBehindMuch() {
        return 0;
    }

    @Override
    public long now() {
        return 0;
    }

    @Override
    public int cleanUnusedTopic(Set<String> topics) {
        return 0;
    }

    @Override
    public void cleanExpiredConsumerQueue() {

    }

    @Override
    public boolean checkInDiskByConsumeOffset(String topic, int queueId, long consumeOffset) {
        return false;
    }

    @Override
    public long dispatchBehindBytes() {
        return 0;
    }

    @Override
    public long flush() {
        return 0;
    }

    @Override
    public boolean resetWriteOffset(long phyOffset) {
        return false;
    }

    @Override
    public long getConfirmOffset() {
        return 0;
    }

    @Override
    public void setConfirmOffset(long phyOffset) {

    }

    @Override
    public boolean isOSPageCacheBusy() {
        return false;
    }

    @Override
    public long lockTimeMills() {
        return 0;
    }

    @Override
    public boolean isTransientStorePoolDeficient() {
        return false;
    }

}
