package com.lemon.takinmq.remoting.provider.domain;

import java.util.concurrent.atomic.AtomicInteger;

import com.lemon.takinmq.remoting.core.support.SystemClock;
import com.lemon.takinmq.remoting.provider.channel.ChannelWrapper;

/**
 * TaskTracker状态对象
 */
public class ConsumerNode {
    // 节点组名称
    public String nodeGroup;
    // 可用线程数
    public AtomicInteger availableThread;
    // 唯一标识
    public String identity;
    // 该节点的channel
    public ChannelWrapper channel;

    public Long timestamp = SystemClock.now();

    public ConsumerNode(String nodeGroup, int availableThread, String identity, ChannelWrapper channel) {
        this.nodeGroup = nodeGroup;
        this.availableThread = new AtomicInteger(availableThread);
        this.identity = identity;
        this.channel = channel;
    }

    public ConsumerNode(String identity) {
        this.identity = identity;
    }

    public ConsumerNode(String identity, String nodeGroup) {
        this.nodeGroup = nodeGroup;
        this.identity = identity;
    }

    public String getNodeGroup() {
        return nodeGroup;
    }

    public void setNodeGroup(String nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    public AtomicInteger getAvailableThread() {
        return availableThread;
    }

    public void setAvailableThread(int availableThread) {
        this.availableThread = new AtomicInteger(availableThread);
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public ChannelWrapper getChannel() {
        return channel;
    }

    public void setChannel(ChannelWrapper channel) {
        this.channel = channel;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ConsumerNode))
            return false;

        ConsumerNode that = (ConsumerNode) o;

        if (identity != null ? !identity.equals(that.identity) : that.identity != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return identity != null ? identity.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TaskTrackerNode{" + "nodeGroup='" + nodeGroup + '\'' + ", availableThread=" + (availableThread == null ? 0 : availableThread.get()) + ", identity='" + identity + '\'' + ", channel=" + channel + '}';
    }
}