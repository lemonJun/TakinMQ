package com.lemon.takinmq.store;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.lemon.takinmq.common.BrokerConfig;
import com.lemon.takinmq.common.ThreadFactoryImpl;
import com.lemon.takinmq.common.datainfo.PutMessageResult;
import com.lemon.takinmq.common.datainfo.PutMessageStatus;
import com.lemon.takinmq.common.message.MessageExt;
import com.lemon.takinmq.common.message.PullResult;
import com.lemon.takinmq.common.util.SystemClock;
import com.lemon.takinmq.store.config.BrokerRole;
import com.lemon.takinmq.store.config.MessageStoreConfig;
import com.lemon.takinmq.store.leveldb.CommitLog;

/**
 * 
 *
 * @author WangYazhou
 * @date  2017年2月18日 上午11:32:10
 * @see
 */
public class DefaultMessageStore implements MessageStore {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageStore.class);

    private final MessageStoreConfig messageStoreConfig;

    //存入的是每一个主题 下的队列ID与消费进度情况
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, ConsumeQueue>> consumeMap = new ConcurrentHashMap<>();

    private final RunningFlags runningFlags = new RunningFlags();
    private final SystemClock systemClock = SystemClock.instance();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("StoreScheduledThread"));

    private final BrokerConfig brokerConfig;

    private volatile boolean shutdown = false;
    private AtomicLong printTimes = new AtomicLong(0);

    private final CommitLog msgQueue;

    public DefaultMessageStore(final MessageStoreConfig messageStoreConfig, final BrokerConfig brokerConfig) {
        this.messageStoreConfig = messageStoreConfig;
        this.brokerConfig = brokerConfig;
        this.msgQueue = new CommitLog(this);
        logger.info("default message store init succ");
    }

    @Override
    public boolean load() {
        return true;
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
        try {
            Stopwatch watch = Stopwatch.createUnstarted();
            if (this.shutdown) {//停了  就不接受消息了 
                logger.warn("message store has shutdown, so putMessage is forbidden");
                return new PutMessageResult(PutMessageStatus.SERVICE_NOT_AVAILABLE);
            }

            //如果是从   则不接受写消息  ？   这样怎么样做到数据HA的
            if (BrokerRole.SLAVE == this.messageStoreConfig.getBrokerRole()) {
                long value = this.printTimes.getAndIncrement();
                if ((value % 50000) == 0) {
                    logger.warn("message store is slave mode, so putMessage is forbidden ");
                }
                return new PutMessageResult(PutMessageStatus.SERVICE_NOT_AVAILABLE);
            }

            if (!this.runningFlags.isWriteable()) {
                long value = this.printTimes.getAndIncrement();
                if ((value % 50000) == 0) {
                    logger.warn("message store is not writeable, so putMessage is forbidden " + this.runningFlags.getFlagBits());
                }

                return new PutMessageResult(PutMessageStatus.SERVICE_NOT_AVAILABLE);
            } else {
                this.printTimes.set(0);
            }

            if (msg.getTopic().length() > Byte.MAX_VALUE) {
                logger.warn("putMessage message topic length too long " + msg.getTopic().length());
                return new PutMessageResult(PutMessageStatus.MESSAGE_ILLEGAL);
            }

            if (msg.getPropertiesString() != null && msg.getPropertiesString().length() > Short.MAX_VALUE) {
                logger.warn("putMessage message properties length too long " + msg.getPropertiesString().length());
                return new PutMessageResult(PutMessageStatus.PROPERTIES_SIZE_EXCEEDED);
            }
            watch.start();
            //        if (this.isOSPageCacheBusy()) {
            //            return new PutMessageResult(PutMessageStatus.OS_PAGECACHE_BUSY);
            //        }
            PutMessageResult result = msgQueue.putMessage(msg);
            watch.stop();
            return result;
        } catch (Exception e) {
            logger.error("put msg error", e);
        }
        return new PutMessageResult(PutMessageStatus.UNKNOWN_ERROR);
    }

    public SystemClock getSystemClock() {
        return systemClock;
    }

    @Override
    public PullResult getMessage(String group, String topic, int queueId, long offset, int maxMsgNums, String subscriptionData) {
        return this.msgQueue.readMessage(topic);
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
