package com.lemon.takinmq.store;

import java.util.HashMap;
import java.util.Set;

import com.lemon.takinmq.common.heartbeat.SubscriptionData;
import com.lemon.takinmq.common.message.MessageExt;

public class DefaultMessageStore implements MessageStore {

    private final StoreStatsService storeStatService;

    public DefaultMessageStore() {
        this.storeStatService = new StoreStatsService();
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
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getOffsetInQueueByTime(String topic, int queueId, long timestamp) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public MessageExt lookMessageByOffset(long commitLogOffset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SelectMappedBufferResult selectOneMessageByOffset(long commitLogOffset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SelectMappedBufferResult selectOneMessageByOffset(long commitLogOffset, int msgSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRunningDataInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HashMap<String, String> getRuntimeInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getMaxPhyOffset() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getMinPhyOffset() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getEarliestMessageTime(String topic, int queueId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getEarliestMessageTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getMessageStoreTimeStamp(String topic, int queueId, long offset) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getMessageTotalInQueue(String topic, int queueId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public SelectMappedBufferResult getCommitLogData(long offset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean appendToCommitLog(long startOffset, byte[] data) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void excuteDeleteFilesManualy() {
        // TODO Auto-generated method stub

    }

    @Override
    public QueryMessageResult queryMessage(String topic, String key, int maxNum, long begin, long end) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateHaMasterAddress(String newAddr) {
        // TODO Auto-generated method stub

    }

    @Override
    public long slaveFallBehindMuch() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long now() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int cleanUnusedTopic(Set<String> topics) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void cleanExpiredConsumerQueue() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean checkInDiskByConsumeOffset(String topic, int queueId, long consumeOffset) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public long dispatchBehindBytes() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long flush() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean resetWriteOffset(long phyOffset) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public long getConfirmOffset() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setConfirmOffset(long phyOffset) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isOSPageCacheBusy() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public long lockTimeMills() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isTransientStorePoolDeficient() {
        // TODO Auto-generated method stub
        return false;
    }

}
