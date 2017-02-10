/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.lemon.takinmq.store;

import java.util.HashMap;
import java.util.Set;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.SubscriptionData;

/**
 * 存储层对外提供的接口
 * @author lemon
 */
public interface MessageStore {

    //重启时，加载数据
    boolean load();

    //启动服务
    void start() throws Exception;

    //关闭服务
    void shutdown();

    //删除所有文件，单元测试会使用
    void destroy();

    //存储消息
    PutMessageResult putMessage(final MessageExtBrokerInner msg);

    //读取消息，如果types为null，则不做过滤
    GetMessageResult getMessage(final String group, final String topic, final int queueId, final long offset, final int maxMsgNums, final SubscriptionData subscriptionData);

    //获取指定队列最大Offset 如果队列不存在，返回-1
    long getMaxOffsetInQuque(final String topic, final int queueId);

    //获取指定队列最小Offset 如果队列不存在，返回-1
    long getMinOffsetInQuque(final String topic, final int queueId);

    //获取消费队列记录的CommitLog Offset
    long getCommitLogOffsetInQueue(final String topic, final int queueId, final long cqOffset);

    //根据消息时间获取某个队列中对应的offset 1、如果指定时间（包含之前之后）有对应的消息，则获取距离此时间最近的offset（优先选择之前）
    //2、如果指定时间无对应消息，则返回0
    long getOffsetInQueueByTime(final String topic, final int queueId, final long timestamp);

    //通过物理队列Offset，查询消息。 如果发生错误，则返回null
    MessageExt lookMessageByOffset(final long commitLogOffset);

    //通过物理队列Offset，查询消息。 如果发生错误，则返回null
    SelectMappedBufferResult selectOneMessageByOffset(final long commitLogOffset);

    SelectMappedBufferResult selectOneMessageByOffset(final long commitLogOffset, final int msgSize);

    //获取运行时统计数据
    String getRunningDataInfo();

    //获取运行时统计数据
    HashMap<String, String> getRuntimeInfo();

    //获取物理队列最大offset
    long getMaxPhyOffset();

    long getMinPhyOffset();

    //获取队列中最早的消息时间
    long getEarliestMessageTime(final String topic, final int queueId);

    long getEarliestMessageTime();

    long getMessageStoreTimeStamp(final String topic, final int queueId, final long offset);

    //获取队列中的消息总数
    long getMessageTotalInQueue(final String topic, final int queueId);

    //数据复制使用：获取CommitLog数据
    SelectMappedBufferResult getCommitLogData(final long offset);

    //数据复制使用：向CommitLog追加数据，并分发至各个Consume Queue
    boolean appendToCommitLog(final long startOffset, final byte[] data);

    //手动触发删除文件
    void excuteDeleteFilesManualy();

    //根据消息Key查询消息
    QueryMessageResult queryMessage(final String topic, final String key, final int maxNum, final long begin, final long end);

    void updateHaMasterAddress(final String newAddr);

    // Slave落后Master多少，单位字节
    long slaveFallBehindMuch();

    long now();

    int cleanUnusedTopic(final Set<String> topics);

    //清除失效的消费队列
    void cleanExpiredConsumerQueue();

    //判断消息是否在磁盘
    boolean checkInDiskByConsumeOffset(final String topic, final int queueId, long consumeOffset);

    long dispatchBehindBytes();

    long flush();

    boolean resetWriteOffset(long phyOffset);

    long getConfirmOffset();

    void setConfirmOffset(long phyOffset);

    boolean isOSPageCacheBusy();

    long lockTimeMills();

    boolean isTransientStorePoolDeficient();
}
