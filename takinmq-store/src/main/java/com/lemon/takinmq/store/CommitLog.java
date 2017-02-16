package com.lemon.takinmq.store;

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

    public CommitLog(final DefaultMessageStore defaultMessageStore) {
        this.defaultMessageStore = defaultMessageStore;
    }

}
