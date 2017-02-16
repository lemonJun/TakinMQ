package com.lemon.takinmq.store;

import com.lemon.takinmq.common.sysflag.MessageSysFlag;
import com.lemon.takinmq.common.util.UtilAll;

/**
 * 提交日志
 * 类似于LEVELDB的操作日志
 *
 * @author WangYazhou
 * @date  2017年2月16日 下午1:50:16
 * @see
 */
public class CommitLog {

    private final DefaultMessageStore defaultMessageStore;

    private volatile long beginTimeInLock = 0;

    public CommitLog(final DefaultMessageStore defaultMessageStore) {
        this.defaultMessageStore = defaultMessageStore;
    }

    public long getBeginTimeInLock() {
        return beginTimeInLock;
    }

    //真正的插入消息操作
    public PutMessageResult putMessage(final MessageExtBrokerInner msg) {
        // 设置存储时间
        msg.setStoreTimestamp(System.currentTimeMillis());
        msg.setBodyCRC(UtilAll.crc32(msg.getBody()));
        // Back to Results
        AppendMessageResult result = null;
        String topic = msg.getTopic();
        int queueId = msg.getQueueId();
        final int tranType = MessageSysFlag.getTransactionValue(msg.getSysFlag());
        //非事务消息处理
        if (tranType == MessageSysFlag.TRANSACTION_NOT_TYPE || tranType == MessageSysFlag.TRANSACTION_COMMIT_TYPE) {
            
        }
        return null;
    }

}
